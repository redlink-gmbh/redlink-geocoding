/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.nominatim;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.prependIfMissing;
import static org.apache.commons.lang3.StringUtils.removeEnd;

/**
 * Geocoder backed with OpenStreetMaps (Nominatim)
 */
public class NominatimGeocoder implements Geocoder {

    public static final String PUBLIC_NOMINATIM_SERVER = "http://nominatim.openstreetmap.org/";
    private static final String SERVICE_GEOCODE = "/search",
            SERVICE_REVERSE = "/reverse",
            SERVICE_LOOKUP = "/lookup";

    private static final String PARAM_QUERY = "q",
            PARAM_PLACEID = "osm_ids",
            PARAM_LAT = "lat",
            PARAM_LON = "lon",
            PARAM_LANG = "accept-language",
            PARAM_EMAIL = "email",
            PARAM_FORMAT = "format";

    private final URI baseUrl;
    private final Locale language;
    private final String email;

    public NominatimGeocoder() {
        this(PUBLIC_NOMINATIM_SERVER);
    }

    protected NominatimGeocoder(String baseUrl, Locale language, String email) {
        this.baseUrl = URI.create(baseUrl);
        this.language = language;
        this.email = email;
    }

    public NominatimGeocoder(String baseUrl) {
        this(baseUrl, null, null);
    }

    public static NominatimBuilder configure() {
        return new NominatimBuilder();
    }

    @Override
    public List<Place> geocode(String address) throws IOException {
        try (CloseableHttpClient client = createHttpClient()) {
            final URI uri = createUriBuilder(SERVICE_GEOCODE)
                    .setParameter(PARAM_QUERY, address)
                    .build();
            final HttpGet request = new HttpGet(uri);
            return client.execute(request, new JsoupResponseHandler<List<Place>>(uri) {
                @Override
                protected List<Place> parseJsoup(Document doc) {
                    return doc.select("searchresults place").stream()
                            .map(NominatimGeocoder::readPlace)
                            .collect(Collectors.toList());
                }
            });
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public List<Place> reverseGeocode(LatLon coordinates) throws IOException {
        try (CloseableHttpClient client = createHttpClient()) {
            final URI uri = createUriBuilder(SERVICE_REVERSE)
                    .setParameter(PARAM_LAT, String.valueOf(coordinates.lat()))
                    .setParameter(PARAM_LON, String.valueOf(coordinates.lon()))
                    .build();
            final HttpGet request = new HttpGet(uri);
            return Collections.singletonList(client.execute(request, new JsoupResponseHandler<Place>(uri) {
                @Override
                protected Place parseJsoup(Document doc) {
                    final Element result = doc.select("reversegeocode result").first();
                    final String id = createPlaceId(result);
                    final Place place = new Place(id);
                    place.setAddress(result.text());
                    place.setLatLon(coordinates);
                    return place;
                }
            }));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private static String createPlaceId(Element element) {
        final String type = element.attr("osm_type"), osmId = element.attr("osm_id");
        return String.format(Locale.ENGLISH, "%S%s", type.substring(0, 1), osmId);
    }

    @Override
    public Place lookup(String placeId) throws IOException {
        try (CloseableHttpClient client = createHttpClient()) {
            final URI uri = createUriBuilder(SERVICE_LOOKUP)
                    .setParameter(PARAM_PLACEID, placeId)
                    .build();
            final HttpGet request = new HttpGet(uri);
            return client.execute(request, new JsoupResponseHandler<Place>(uri) {
                @Override
                protected Place parseJsoup(Document doc) {
                    return readPlace(doc.select("lookupresults place").first());
                }
            });
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private static Place readPlace(Element element) {
        final String id = createPlaceId(element);
        final Place place = new Place(id);
        place.setAddress(element.attr("display_name"));
        place.setLatLon(LatLon.valueOf(element.attr("lat"), element.attr("lon")));
        return place;
    }

    private URIBuilder createUriBuilder(String service) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(removeEnd(baseUrl.toString(), "/") + prependIfMissing(service, "/"))
                .setParameter(PARAM_FORMAT, "xml");
        if (StringUtils.isNotBlank(email)) {
            uriBuilder.setParameter(PARAM_EMAIL, email);
        }
        if (Objects.nonNull(language)) {
            uriBuilder.setParameter(PARAM_LANG, language.getLanguage());
        }
        return uriBuilder;
    }

    private CloseableHttpClient createHttpClient() {
        return HttpClientBuilder.create().build();
    }

    private static abstract class JsoupResponseHandler<T> implements ResponseHandler<T> {
        private final URI requestUri;

        public JsoupResponseHandler(URI requestUri) {
            this.requestUri = requestUri;
        }

        @Override
        public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                final Document document = Jsoup.parse(response.getEntity().getContent(), "utf-8", null, Parser.xmlParser());
                return parseJsoup(document);
            } else
                throw new IOException("Got HTTP-" + statusCode + " when requesting " + requestUri.toASCIIString());
        }

        protected abstract T parseJsoup(Document jsoupDocument);
    }
}

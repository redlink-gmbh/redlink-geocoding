/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.nominatim;

import com.google.common.util.concurrent.RateLimiter;
import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
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
    protected static final String SERVICE_GEOCODE = "/search";
    protected static final String SERVICE_REVERSE = "/reverse";
    protected static final String SERVICE_LOOKUP = "/lookup";

    protected static final String PARAM_QUERY = "q",
            PARAM_PLACE_ID = "osm_ids",
            PARAM_LAT = "lat",
            PARAM_LON = "lon",
            PARAM_LANG = "accept-language",
            PARAM_EMAIL = "email",
            PARAM_FORMAT = "format";

    private final URI baseUrl;
    private final Locale language;
    private final String email;
    private final Proxy proxy;
    private final RateLimiter rateLimiter;
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected NominatimGeocoder() {
        this(PUBLIC_NOMINATIM_SERVER);
    }

    protected NominatimGeocoder(String baseUrl) {
        this(baseUrl, null, null, null);
    }

    protected NominatimGeocoder(String baseUrl, Locale language, String email, Proxy proxy) {
        this(baseUrl, language, email, proxy, -1);
    }

    protected NominatimGeocoder(String baseUrl, Locale language, String email, Proxy proxy, int maxQps) {
        this.baseUrl = URI.create(baseUrl);
        this.language = language;
        this.email = email;
        this.proxy = proxy;
        if (maxQps < 0 && StringUtils.equals(baseUrl, PUBLIC_NOMINATIM_SERVER)) {
            // set default qps for public server
            maxQps = 1;
        }
        if (maxQps > 0) {
            rateLimiter = RateLimiter.create(maxQps);
        } else {
            rateLimiter = null;
        }
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
                    return Place.create(createPlaceId(result), result.text(), coordinates);
                }
            }));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public Place lookup(String placeId) throws IOException {
        try (CloseableHttpClient client = createHttpClient()) {
            final URI uri = createUriBuilder(SERVICE_LOOKUP)
                    .setParameter(PARAM_PLACE_ID, placeId)
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
        return Place.create(createPlaceId(element),
                element.attr("display_name"),
                LatLon.valueOf(element.attr("lat"), element.attr("lon")));
    }

    private static String createPlaceId(Element element) {
        final String type = element.attr("osm_type"), osmId = element.attr("osm_id");
        return String.format(Locale.ENGLISH, "%S%s", type.substring(0, 1), osmId);
    }

    protected URIBuilder createUriBuilder(String service) throws URISyntaxException {
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

    protected CloseableHttpClient createHttpClient() {
        if (rateLimiter != null) rateLimiter.acquire();
        final HttpClientBuilder builder = HttpClientBuilder.create();
        if (proxy == null || proxy.type() == Proxy.Type.DIRECT) {
            log.trace("Direct Connection");
        } else if (proxy.type() == Proxy.Type.HTTP) {
            final InetSocketAddress proxyAddress = (InetSocketAddress) proxy.address();
            final HttpHost proxyHost = new HttpHost(proxyAddress.getAddress(), proxyAddress.getPort());
            builder.setProxy(proxyHost);
            log.debug("Using Proxy {}", proxyHost);
        } else {
            log.warn("Unsupported Proxy-Type {}, fallback to DIRECT connection", proxy.type());
        }
        return builder.build();
    }

    
    
    @Override
    public String toString() {
        return "NominatimGeocoder [baseUrl=" + baseUrl + ", language=" + language + ", email=" + email
                + ", rateLimiter=" + rateLimiter + "]";
    }



    protected static abstract class JsoupResponseHandler<T> implements ResponseHandler<T> {
        private final URI requestUri;

        public JsoupResponseHandler(URI requestUri) {
            this.requestUri = requestUri;
        }

        @Override
        public T handleResponse(HttpResponse response) throws IOException {
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                final Document document = Jsoup.parse(response.getEntity().getContent(), "utf-8", requestUri.toString(), Parser.xmlParser());
                return parseJsoup(document);
            } else
                throw new IOException("Got HTTP-" + statusCode + " when requesting " + requestUri.toASCIIString());
        }

        protected abstract T parseJsoup(Document jsoupDocument);
    }
}

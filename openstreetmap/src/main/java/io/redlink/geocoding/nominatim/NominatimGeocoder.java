/*
 * Copyright (c) 2017-2022 Redlink GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.redlink.geocoding.nominatim;

import com.google.common.util.concurrent.RateLimiter;
import io.redlink.geocoding.AddressComponent;
import io.redlink.geocoding.AddressComponent.Type;
import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.net.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.prependIfMissing;
import static org.apache.commons.lang3.StringUtils.removeEnd;

/**
 * Geocoder backed with OpenStreetMaps (Nominatim)
 */
public class NominatimGeocoder implements Geocoder {

    private static final Logger LOG = LoggerFactory.getLogger(NominatimGeocoder.class);

    public static final String PUBLIC_NOMINATIM_SERVER = "https://nominatim.openstreetmap.org/";
    protected static final String SERVICE_GEOCODE = "/search";
    protected static final String SERVICE_REVERSE = "/reverse";
    protected static final String SERVICE_LOOKUP = "/lookup";

    protected static final String PARAM_QUERY = "q";
    protected static final String PARAM_PLACE_ID = "osm_ids";
    protected static final String PARAM_LAT = "lat";
    protected static final String PARAM_LON = "lon";
    protected static final String PARAM_LANG = "accept-language";
    protected static final String PARAM_EMAIL = "email";
    protected static final String PARAM_FORMAT = "format";
    protected static final String PARAM_PLACEDETAILS = "addressdetails";

    private final URI baseUrl;
    private final Locale language;
    private final String email;
    private final Proxy proxy;
    private final RateLimiter rateLimiter;

    /**
     * @deprecated use {@link #builder()}
     */
    @Deprecated(since = "2.0.2")
    protected NominatimGeocoder() {
        this(PUBLIC_NOMINATIM_SERVER);
    }

    /**
     * @deprecated use {@link #builder()}
     */
    @Deprecated(since = "2.0.2")
    protected NominatimGeocoder(String baseUrl) {
        this(baseUrl, null, null, null);
    }

    /**
     * @deprecated use {@link #builder()}
     */
    @Deprecated(since = "2.0.2")
    protected NominatimGeocoder(String baseUrl, Locale language, String email, Proxy proxy) {
        this(baseUrl, language, email, proxy, -1);
    }

    /**
     * @deprecated use {@link #builder()}
     */
    @Deprecated(since = "2.0.2")
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

    /**
     * @deprecated use {@link #builder()}
     */
    @Deprecated(since = "2.0.2", forRemoval = true)
    public static NominatimBuilder configure() {
        return builder();
    }

    @SuppressWarnings("deprecation")
    public static NominatimBuilder builder() {
        return new NominatimBuilder();
    }

    @Override
    public List<Place> geocode(String address, Locale lang) throws IOException {
        try (CloseableHttpClient client = createHttpClient()) {
            final URI uri = createUriBuilder(SERVICE_GEOCODE, lang)
                    .setParameter(PARAM_PLACEDETAILS, "1")
                    .setParameter(PARAM_QUERY, address)
                    .build();
            final HttpGet request = new HttpGet(uri);
            final List<Place> places = client.execute(request, new JsoupResponseHandler<>(uri) {
                @Override
                protected List<Place> parseJsoup(Document doc) {
                    return doc.select("place").stream()
                            .map(NominatimGeocoder.this::readPlace)
                            .flatMap(Optional::stream)
                            .collect(Collectors.toList());
                }
            });
            LOG.debug("Geocoding '{}' resulted in {} places", address, places.size());
            return places;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public List<Place> reverseGeocode(LatLon coordinates, Locale lang) throws IOException {
        try (CloseableHttpClient client = createHttpClient()) {
            final URI uri = createUriBuilder(SERVICE_REVERSE, lang)
                    .setParameter(PARAM_LAT, String.valueOf(coordinates.lat()))
                    .setParameter(PARAM_LON, String.valueOf(coordinates.lon()))
                    .build();
            final HttpGet request = new HttpGet(uri);
            final List<Place> places = client.execute(request, new JsoupResponseHandler<>(uri) {
                @Override
                protected List<Place> parseJsoup(Document doc) {
                    final Element result = doc.selectFirst("reversegeocode result");
                    if (result == null) {
                        return List.of();
                    } else {
                        return List.of(
                                Place.create(createPlaceId(result), result.text(), coordinates)
                        );
                    }
                }
            });
            LOG.debug("Reverse-Geocoding '{}' resulted in {} places", coordinates, places.size());
            return places;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Place> lookup(String placeId, Locale lang) throws IOException {
        try (CloseableHttpClient client = createHttpClient()) {
            final URI uri = createUriBuilder(SERVICE_LOOKUP, lang)
                    .setParameter(PARAM_PLACEDETAILS, "1")
                    .setParameter(PARAM_PLACE_ID, placeId)
                    .build();
            final HttpGet request = new HttpGet(uri);
            final Optional<Place> place = client.execute(request, new JsoupResponseHandler<>(uri) {
                @Override
                protected Optional<Place> parseJsoup(Document doc) {
                    return readPlace(doc.selectFirst("place"));
                }
            });
            LOG.debug("Lookup of {} resulted in {}", placeId, place);
            return place;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * The element names of place details are different between different regions.
     * While some names are very common especially the {@link Type#city} level uses
     * different names in different regions and is in fact missing for some (e.g. Berlin)
     */
    Optional<Place> readPlace(Element element) {
        if (element == null) {
            return Optional.empty();
        }

        final String placeId = createPlaceId(element);
        final String displayName = element.attr("display_name");
        if (StringUtils.isAnyBlank(placeId, displayName)) {
            return Optional.empty();
        }

        final LatLon latLon;
        try {
            latLon = LatLon.valueOf(element.attr("lat"), element.attr("lon"));
        } catch (IllegalArgumentException e) {
            LOG.trace("Ignoring place {} with invalid coordinates: {}", placeId, e.getMessage());
            return Optional.empty();
        }

        final Map<Type, AddressComponent> components = new EnumMap<>(Type.class);
        final Map<String, String> metadata = new HashMap<>();
        element.children().forEach(child -> {
            final String value = child.text();
            if (StringUtils.isBlank(value)) {
                return;
            }
            switch (child.nodeName()) {
                case "country_code":
                    components.put(Type.countryCode, AddressComponent.create(Type.countryCode, value));
                    break;
                case "country":
                    components.put(Type.country, AddressComponent.create(Type.country, value));
                    break;
                case "postcode":
                    components.put(Type.postalCode, AddressComponent.create(Type.postalCode, value));
                    break;
                case "state":
                    components.put(Type.state, AddressComponent.create(Type.state, value));
                    break;
                case "village":
                case "city":
                case "town":
                    components.put(Type.city, AddressComponent.create(Type.city, value));
                    break;
                case "city_district":
                    //case "suburb":
                    //case "neighbourhood":
                    components.put(Type.sublocality, AddressComponent.create(Type.sublocality, value));
                    break;
                case "road":
                    components.put(Type.street, AddressComponent.create(Type.street, value));
                    break;
                case "house_number":
                    components.put(Type.streetNumber, AddressComponent.create(Type.streetNumber, value));
                    break;
                default: //add unmapped fields to the metadata
                    metadata.put(child.nodeName(), value);
            }
        });

        return Optional.of(
                Place.create(placeId, displayName, latLon, Set.copyOf(components.values()), metadata)
        );
    }

    private static String createPlaceId(Element element) {
        final String type = element.attr("osm_type");
        final String osmId = element.attr("osm_id");
        if (StringUtils.isAnyBlank(type, osmId)) {
            return null;
        }
        return String.format(Locale.ENGLISH, "%S%s", type.charAt(0), osmId);
    }

    protected URIBuilder createUriBuilder(String service, Locale lang) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(removeEnd(baseUrl.toString(), "/") + prependIfMissing(service, "/"))
                .setParameter(PARAM_FORMAT, "xml");
        if (StringUtils.isNotBlank(email)) {
            uriBuilder.setParameter(PARAM_EMAIL, email);
        }
        if (Objects.nonNull(lang)) {
            uriBuilder.setParameter(PARAM_LANG, lang.toLanguageTag());
        } else if (Objects.nonNull(language)) {
            uriBuilder.setParameter(PARAM_LANG, language.toLanguageTag());
        }
        return uriBuilder;
    }

    protected CloseableHttpClient createHttpClient() {
        if (rateLimiter != null) rateLimiter.acquire();
        final HttpClientBuilder builder = HttpClients.custom();
        if (proxy == null || proxy.type() == Proxy.Type.DIRECT) {
            LOG.trace("Direct Connection");
        } else if (proxy.type() == Proxy.Type.HTTP) {
            final InetSocketAddress proxyAddress = (InetSocketAddress) proxy.address();
            final HttpHost proxyHost = new HttpHost(proxyAddress.getAddress(), proxyAddress.getPort());
            builder.setProxy(proxyHost);
            LOG.debug("Using Proxy {}", proxyHost);
        } else {
            LOG.warn("Unsupported Proxy-Type {}, fallback to DIRECT connection", proxy.type());
        }
        return builder.build();
    }

    @Override
    public String toString() {
        return "NominatimGeocoder [baseUrl=" + baseUrl + ", language=" + language + ", email=" + email
                + ", rateLimiter=" + rateLimiter + "]";
    }


    protected abstract static class JsoupResponseHandler<T> implements HttpClientResponseHandler<T> {
        private final URI requestUri;

        protected JsoupResponseHandler(URI requestUri) {
            this.requestUri = requestUri;
        }

        @Override
        public T handleResponse(ClassicHttpResponse response) throws IOException {
            final int statusCode = response.getCode();
            if (statusCode >= 200 && statusCode < 300) {
                final Document document = Jsoup.parse(response.getEntity().getContent(), "utf-8", requestUri.toString(), Parser.xmlParser());
                return parseJsoup(document);
            } else
                throw new IOException("Got HTTP-" + statusCode + " when requesting " + requestUri.toASCIIString());
        }

        protected abstract T parseJsoup(Document jsoupDocument);
    }
}

/*
 * Copyright (c) 2021 Redlink GmbH.
 */
package io.redlink.geocoding.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import io.redlink.geocoding.proxy.io.Endpoints;
import io.redlink.geocoding.proxy.io.PlaceDTO;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyGeocoder implements Geocoder, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyGeocoder.class);

    private final URI baseUri;
    private final Locale language;
    private final boolean internalHttpClient;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    ProxyGeocoder(URI baseUri, Locale language, CloseableHttpClient httpClient) {
        this.baseUri = baseUri;
        this.language = language;
        this.internalHttpClient = httpClient != null;
        this.httpClient = Objects.requireNonNullElseGet(httpClient,
                () -> HttpClientBuilder.create().useSystemProperties().build()
        );
        objectMapper = JsonMapper.builder().build();

    }

    ProxyGeocoder(URI baseUri) {
        this(baseUri, null);
    }

    ProxyGeocoder(URI baseUri, Locale language) {
        this(baseUri, language, null);
    }

    public static ProxyBuilder configure() {
        return new ProxyBuilder();
    }

    @Override
    public List<Place> geocode(String address, Locale lang) throws IOException {
        try {
            final URIBuilder b = createUriBuilder(Endpoints.GEOCODE, lang);
            b.setParameter(Endpoints.PARAM_ADDRESS, address);

            final HttpGet request = new HttpGet(b.build());
            final List<Place> places = httpClient.execute(request, this::readPlaceList)
                    .stream()
                    .map(PlaceDTO::toPlace)
                    .collect(Collectors.toList());
            LOG.debug("Geocoding '{}' resulted in {} places", address, places.size());
            return places;
        } catch (URISyntaxException e) {
            throw createIOException(Endpoints.GEOCODE, e);
        }
    }

    @Override
    public List<Place> reverseGeocode(LatLon coordinates, Locale lang) throws IOException {
        try {
            URIBuilder b = createUriBuilder(Endpoints.REVERSE_GEOCODE, lang);
            b.setParameter(Endpoints.PARAM_LAT, String.valueOf(coordinates.lat()));
            b.setParameter(Endpoints.PARAM_LON, String.valueOf(coordinates.lon()));

            final HttpGet request = new HttpGet(b.build());
            final List<Place> places = httpClient.execute(request, this::readPlaceList)
                    .stream()
                    .map(PlaceDTO::toPlace)
                    .collect(Collectors.toList());
            LOG.debug("Reverse-Geocoding '{}' resulted in {} places", coordinates, places.size());
            return places;
        } catch (URISyntaxException e) {
            throw createIOException(Endpoints.REVERSE_GEOCODE, e);
        }
    }

    @Override
    public Optional<Place> lookup(String placeId, Locale lang) throws IOException {
        try {
            URIBuilder b = createUriBuilder(Endpoints.LOOKUP, lang);
            b.setParameter(Endpoints.PARAM_PLACE_ID, placeId);

            final HttpGet request = new HttpGet(b.build());
            final Optional<Place> place = Optional.ofNullable(
                    httpClient.execute(request, (HttpResponse response) -> {
                        if (isSuccess(response)) {
                            return objectMapper.readValue(response.getEntity().getContent(),
                                    PlaceDTO.class);
                        } else if (isNotFound(response)) {
                            return null;
                        }
                        throw createIOException(response);
                    })
            ).map(PlaceDTO::toPlace);
            LOG.debug("Lookup of {} resulted in {}", placeId, place);
            return place;
        } catch (URISyntaxException e) {
            throw createIOException(Endpoints.LOOKUP, e);
        }
    }

    private URIBuilder createUriBuilder(String service, Locale lang) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(
                String.join("/", Arrays.asList(
                        removeEnd(baseUri.toString(), "/"),
                        Endpoints.API_VERSION,
                        service
                )));
        if (Objects.nonNull(lang)) {
            uriBuilder.setParameter(Endpoints.PARAM_LANG, lang.toLanguageTag());
        } else if (Objects.nonNull(language)) {
            uriBuilder.setParameter(Endpoints.PARAM_LANG, language.toLanguageTag());
        }
        return uriBuilder;
    }

    private List<PlaceDTO> readPlaceList(HttpResponse response) throws IOException {
        if (isSuccess(response)) {
            return objectMapper.readValue(response.getEntity().getContent(),
                    objectMapper.getTypeFactory().constructCollectionLikeType(List.class, PlaceDTO.class));
        }
        throw createIOException(response);
    }

    private IOException createIOException(HttpResponse response) {
        return new IOException(String.format("Could not read result from server response: HTTP-%d (%s)",
                response.getStatusLine().getStatusCode(),
                response.getStatusLine().getReasonPhrase()
        ));
    }

    private IOException createIOException(String endpoint, URISyntaxException e) {
        return new IOException(String.format("Could not build request uri for %s", endpoint), e);
    }

    private boolean isSuccess(HttpResponse response) {
        final int statusCode = response.getStatusLine().getStatusCode();
        return statusCode >= 200 && statusCode < 300 && Objects.nonNull(response.getEntity());
    }

    private boolean isNotFound(HttpResponse response) {
        final int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 404;
    }

    @Override
    public void close() throws IOException {
        if (internalHttpClient) {
            httpClient.close();
        }
    }

    @Override
    public String toString() {
        return "ProxyGeocoder [baseUri=" + baseUri + ", language=" + language + ']';
    }

    private static String removeEnd(String string, String suffix) {
        if (string == null) {
            return null;
        } else if (string.endsWith(suffix)) {
            return string.substring(0, string.length() - suffix.length());
        } else {
            return string;
        }
    }

}
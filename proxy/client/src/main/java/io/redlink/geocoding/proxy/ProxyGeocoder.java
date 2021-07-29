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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class ProxyGeocoder implements Geocoder, Closeable {

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

    @Override
    public List<Place> geocode(String address, Locale lang) throws IOException {
        try {
            final URIBuilder b = createUriBuilder(Endpoints.GEOCODE, lang);
            b.setParameter(Endpoints.PARAM_ADDRESS, address);

            final HttpGet request = new HttpGet(b.build());
            return httpClient.execute(request, this::readPlaceList)
                    .stream()
                    .map(PlaceDTO::toPlace)
                    .collect(Collectors.toList());
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
            return httpClient.execute(request, this::readPlaceList)
                    .stream()
                    .map(PlaceDTO::toPlace)
                    .collect(Collectors.toList());
        } catch (URISyntaxException e) {
            throw createIOException(Endpoints.REVERSE_GEOCODE, e);
        }
    }

    @Override
    public Place lookup(String placeId, Locale lang) throws IOException {
        try {
            URIBuilder b = createUriBuilder(Endpoints.LOOKUP, lang);
            b.setParameter(Endpoints.PARAM_PLACE_ID, placeId);

            final HttpGet request = new HttpGet(b.build());
            return httpClient.execute(request, (HttpResponse response) -> {
                if (isSuccess(response)) {
                    return objectMapper.readValue(response.getEntity().getContent(),
                            PlaceDTO.class);
                }
                throw createIOException(response);
            }).toPlace();
        } catch (URISyntaxException e) {
            throw createIOException(Endpoints.LOOKUP, e);
        }
    }

    private URIBuilder createUriBuilder(String service, Locale lang) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(removeEnd(baseUri.toString(), "/") + prependIfMissing(service, "/"));
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

    @Override
    public void close() throws IOException {
        if (internalHttpClient) {
            httpClient.close();
        }
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

    private static String prependIfMissing(String string, String prefix) {
        if (string == null) {
            return null;
        } else if (string.startsWith(prefix)) {
            return string;
        } else {
            return prefix + string;
        }
    }
}

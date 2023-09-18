/*
 * Copyright (c) 2023 Redlink GmbH.
 */
package io.redlink.geocoding.nominatim;

import java.util.Map;
import java.util.Objects;

import static io.redlink.geocoding.nominatim.NominatimGeocoder.DEFAULT_GEOCODE_ENDPOINT;
import static io.redlink.geocoding.nominatim.NominatimGeocoder.DEFAULT_LOOKUP_ENDPOINT;
import static io.redlink.geocoding.nominatim.NominatimGeocoder.DEFAULT_REVERSE_ENDPOINT;

class ServiceConfiguration {

    private final String geocodeEndpoint;
    private final String reverseEndpoint;
    private final String lookupEndpoint;

    private final Map<String, String> customQueryParams;
    private final Map<String, String> customHeaders;

    private final String userAgent;

    ServiceConfiguration() {
        this(DEFAULT_GEOCODE_ENDPOINT, DEFAULT_REVERSE_ENDPOINT, DEFAULT_LOOKUP_ENDPOINT, Map.of(), Map.of(), null);
    }

    ServiceConfiguration(String geocodeEndpoint, String reverseEndpoint, String lookupEndpoint,
                         Map<String, String> customQueryParams,
                         Map<String, String> customHeaders,
                         String userAgent) {
        this.geocodeEndpoint = geocodeEndpoint;
        this.reverseEndpoint = reverseEndpoint;
        this.lookupEndpoint = lookupEndpoint;
        this.customQueryParams = Map.copyOf(customQueryParams);
        this.customHeaders = Map.copyOf(customHeaders);
        this.userAgent = userAgent;
    }

    public String getGeocodeEndpoint() {
        return Objects.requireNonNullElse(geocodeEndpoint, DEFAULT_GEOCODE_ENDPOINT);
    }

    public String getReverseEndpoint() {
        return Objects.requireNonNullElse(reverseEndpoint, DEFAULT_REVERSE_ENDPOINT);
    }

    public String getLookupEndpoint() {
        return Objects.requireNonNullElse(lookupEndpoint, DEFAULT_LOOKUP_ENDPOINT);
    }

    public Map<String, String> getCustomQueryParams() {
        return customQueryParams;
    }

    public Map<String, String> getCustomHeaders() {
        return customHeaders;
    }

    public String getUserAgent() {
        return userAgent;
    }

}

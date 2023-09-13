/*
 * Copyright (c) 2023 Redlink GmbH.
 */
package io.redlink.geocoding.nominatim;

import java.util.Map;
import java.util.Objects;

class ServiceConfiguration {

    private final String geocodeEndpoint;
    private final String reverseEndpoint;
    private final String lookupEndpoint;

    private final Map<String, String> customQueryParams;
    private final Map<String, String> customHeaders;

    ServiceConfiguration() {
        this(NominatimGeocoder.SERVICE_GEOCODE, NominatimGeocoder.SERVICE_REVERSE, NominatimGeocoder.SERVICE_LOOKUP, Map.of(), Map.of());
    }

    ServiceConfiguration(String geocodeEndpoint, String reverseEndpoint, String lookupEndpoint,
                                Map<String, String> customQueryParams,
                                Map<String, String> customHeaders) {
        this.geocodeEndpoint = geocodeEndpoint;
        this.reverseEndpoint = reverseEndpoint;
        this.lookupEndpoint = lookupEndpoint;
        this.customQueryParams = Map.copyOf(customQueryParams);
        this.customHeaders = Map.copyOf(customHeaders);
    }

    public String getGeocodeEndpoint() {
        return Objects.requireNonNullElse(geocodeEndpoint, NominatimGeocoder.SERVICE_GEOCODE);
    }

    public String getReverseEndpoint() {
        return Objects.requireNonNullElse(reverseEndpoint, NominatimGeocoder.SERVICE_REVERSE);
    }

    public String getLookupEndpoint() {
        return Objects.requireNonNullElse(lookupEndpoint, NominatimGeocoder.SERVICE_LOOKUP);
    }

    public Map<String, String> getCustomQueryParams() {
        return customQueryParams;
    }

    public Map<String, String> getCustomHeaders() {
        return customHeaders;
    }
}

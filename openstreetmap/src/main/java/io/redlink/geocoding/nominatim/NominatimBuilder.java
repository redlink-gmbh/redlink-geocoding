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

import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A Builder for NominatimGeocoder
 */
public class NominatimBuilder {

    private String baseUrl;
    private String email = null;
    private Locale locale;
    private Proxy proxy = null;
    private int maxQps = -1;

    private String geocodeEndpoint = NominatimGeocoder.DEFAULT_GEOCODE_ENDPOINT;
    private String reverseEndpoint = NominatimGeocoder.DEFAULT_REVERSE_ENDPOINT;
    private String lookupEndpoint = NominatimGeocoder.DEFAULT_LOOKUP_ENDPOINT;

    private final Map<String, String> customQuery = new LinkedHashMap<>();
    private final Map<String, String> customHeaders = new LinkedHashMap<>();

    /**
     * @deprecated use {@link NominatimGeocoder#builder()}
     */
    @Deprecated(since = "2.0.2")
    public NominatimBuilder() {
        baseUrl = NominatimGeocoder.PUBLIC_NOMINATIM_SERVER;
        locale = Locale.getDefault(Locale.Category.DISPLAY);
    }

    public NominatimGeocoder create() {
        return new NominatimGeocoder(baseUrl, locale, email, proxy, maxQps,
                new ServiceConfiguration(
                    geocodeEndpoint, reverseEndpoint, lookupEndpoint,
                    customQuery, customHeaders
                )
        );
    }

    public NominatimBuilder setBaseUrl(URI baseUrl) {
        return setBaseUrl(baseUrl.toASCIIString());
    }

    public NominatimBuilder setBaseUrl(URL baseUrl) {
        return setBaseUrl(baseUrl.toExternalForm());
    }

    public NominatimBuilder setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public NominatimBuilder setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public NominatimBuilder setQueryRateLimit(int maxQps) {
        this.maxQps = maxQps;
        return this;
    }

    public NominatimBuilder setLocale(String lang) {
        this.locale = Locale.forLanguageTag(lang);
        return this;
    }

    public NominatimBuilder setLocale(Locale lang) {
        this.locale = lang;
        return this;
    }

    public NominatimBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public NominatimBuilder setGeocodeEndpoint(String geocodeEndpoint) {
        this.geocodeEndpoint = geocodeEndpoint;
        return this;
    }

    public NominatimBuilder setReverseEndpoint(String reverseEndpoint) {
        this.reverseEndpoint = reverseEndpoint;
        return this;
    }

    public NominatimBuilder setLookupEndpoint(String lookupEndpoint) {
        this.lookupEndpoint = lookupEndpoint;
        return this;
    }

    public NominatimBuilder setStaticQueryParam(String key, String value) {
        this.customQuery.put(key, value);
        return this;
    }

    public NominatimBuilder setStaticHeader(String headerName, String headerValue) {
        this.customHeaders.put(headerName, headerValue);
        return this;
    }
}

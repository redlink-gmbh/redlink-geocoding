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
package io.redlink.geocoding.spring.boot.autoconfigure;

import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 */
@ConfigurationProperties(prefix = "geocoding")
public class GeocodingProperties {

    private GoogleMapsProperties google = new GoogleMapsProperties();

    private NominatimProperties nominatim = new NominatimProperties();

    private ProxyProperties proxyService = new ProxyProperties();

    private Locale lang = Locale.ENGLISH;

    private URL proxy = null;

    /**
     * Cache-Timeout in seconds
     */
    private long cacheTimeout = -1;

    public static class GoogleMapsProperties {

        private String apiKey;
        private String clientId;
        private String cryptoSecret;
        private String channel;

        public String getApiKey() {
            return apiKey;
        }

        public GoogleMapsProperties setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public String getClientId() {
            return clientId;
        }

        public GoogleMapsProperties setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public String getCryptoSecret() {
            return cryptoSecret;
        }

        public GoogleMapsProperties setCryptoSecret(String cryptoSecret) {
            this.cryptoSecret = cryptoSecret;
            return this;
        }

        public String getChannel() {
            return channel;
        }

        public GoogleMapsProperties setChannel(String channel) {
            this.channel = channel;
            return this;
        }
    }

    public static class NominatimProperties {
        private URL baseUrl;

        private NominatimServiceEndpoints endpoints = new NominatimServiceEndpoints();

        private String email;

        private Map<String, String> extraQueryParams = Map.of();

        private Map<String, String> extraHeaders = Map.of();

        public URL getBaseUrl() {
            return baseUrl;
        }

        public NominatimProperties setBaseUrl(URL baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public NominatimServiceEndpoints getEndpoints() {
            return endpoints;
        }

        public NominatimProperties setEndpoints(NominatimServiceEndpoints endpoints) {
            this.endpoints = endpoints;
            return this;
        }

        public String getEmail() {
            return email;
        }

        public NominatimProperties setEmail(String email) {
            this.email = email;
            return this;
        }

        public Map<String, String> getExtraQueryParams() {
            return extraQueryParams;
        }

        public NominatimProperties setExtraQueryParams(Map<String, String> extraQueryParams) {
            this.extraQueryParams = extraQueryParams != null ? Map.copyOf(extraQueryParams): Map.of();
            return this;
        }

        public Map<String, String> getExtraHeaders() {
            return extraHeaders;
        }

        public NominatimProperties setExtraHeaders(Map<String, String> extraHeaders) {
            this.extraHeaders = extraHeaders != null ? Map.copyOf(extraHeaders): Map.of();
            return this;
        }

        public static class NominatimServiceEndpoints {
            private String geocoding;
            private String reverse;
            private String lookup;

            public String getGeocoding() {
                return geocoding;
            }

            public NominatimServiceEndpoints setGeocoding(String geocoding) {
                this.geocoding = geocoding;
                return this;
            }

            public String getReverse() {
                return reverse;
            }

            public NominatimServiceEndpoints setReverse(String reverse) {
                this.reverse = reverse;
                return this;
            }

            public String getLookup() {
                return lookup;
            }

            public NominatimServiceEndpoints setLookup(String lookup) {
                this.lookup = lookup;
                return this;
            }
        }

    }

    public static class ProxyProperties {
        private URI baseUrl;

        public URI getBaseUrl() {
            return baseUrl;
        }

        public ProxyProperties setBaseUrl(URI baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }
    }

    public GoogleMapsProperties getGoogle() {
        return google;
    }

    public GeocodingProperties setGoogle(GoogleMapsProperties google) {
        this.google = google;
        return this;
    }

    public NominatimProperties getNominatim() {
        return nominatim;
    }

    public GeocodingProperties setNominatim(NominatimProperties nominatim) {
        this.nominatim = nominatim;
        return this;
    }

    public ProxyProperties getProxyService() {
        return proxyService;
    }

    public GeocodingProperties setProxyService(ProxyProperties proxyService) {
        this.proxyService = proxyService;
        return this;
    }

    public Locale getLang() {
        return lang;
    }

    public GeocodingProperties setLang(Locale lang) {
        this.lang = lang;
        return this;
    }

    public URL getProxy() {
        return proxy;
    }

    public GeocodingProperties setProxy(URL proxy) {
        this.proxy = proxy;
        return this;
    }

    public long getCacheTimeout() {
        return cacheTimeout;
    }

    public GeocodingProperties setCacheTimeout(long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
        return this;
    }
}

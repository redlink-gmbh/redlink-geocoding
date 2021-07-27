/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.spring.boot.autoconfigure;

import java.net.URL;
import java.util.Locale;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 */
@ConfigurationProperties(prefix = "geocoding")
public class GeocodingProperties {

    private GoogleMapsProperties google = new GoogleMapsProperties();

    private NominatimProperties nominatim = new NominatimProperties();

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
        private String email;

        public URL getBaseUrl() {
            return baseUrl;
        }

        public NominatimProperties setBaseUrl(URL baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public String getEmail() {
            return email;
        }

        public NominatimProperties setEmail(String email) {
            this.email = email;
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

/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.google;

import com.google.maps.GeoApiContext;
import java.net.Proxy;
import java.util.Locale;
import java.util.Objects;

/**
 * A Builder for the GoogleMapsGeocoder
 */
public class GoogleMapsBuilder {

    private final GeoApiContext.Builder context;
    private boolean apiKeySet;
    private boolean cryptoSecretSet;
    private Locale lang;

    public GoogleMapsBuilder() {
        context = new GeoApiContext.Builder();
        lang = Locale.getDefault(Locale.Category.DISPLAY);
    }

    public GoogleMapsBuilder setLocale(String language) {
        lang = Locale.forLanguageTag(language);
        return this;
    }

    public GoogleMapsBuilder setLocale(Locale locale) {
        lang = locale;
        return this;
    }

    public GoogleMapsBuilder setProxy(Proxy proxy) {
        context.proxy(proxy);
        return this;
    }

    public GoogleMapsBuilder setQueryRateLimit(int maxQps) {
        context.queryRateLimit(maxQps);
        return this;
    }

    public GoogleMapsBuilder setApiKey(String apiKey) {
        context.apiKey(apiKey);
        apiKeySet = Objects.nonNull(apiKey);
        return this;
    }

    public GoogleMapsBuilder setChannel(String channel) {
        context.channel(channel);
        return this;
    }

    public GoogleMapsBuilder setCredentials(String clientId, String cryptoSecret) {
        context.enterpriseCredentials(clientId, cryptoSecret);
        cryptoSecretSet = Objects.nonNull(cryptoSecret);
        return this;
    }

    public GoogleMapsGeocoder create() {
        // Check state
        if (cryptoSecretSet || apiKeySet) {
            return new GoogleMapsGeocoder(context.build(), lang, cryptoSecretSet, apiKeySet);
        } else {
            throw new IllegalStateException("Must provide either API key or Maps for Work credentials.");
        }
    }

}

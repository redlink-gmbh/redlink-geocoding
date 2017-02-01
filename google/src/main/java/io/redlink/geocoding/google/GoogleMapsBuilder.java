/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.google;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;

import java.net.Proxy;
import java.util.Locale;
import java.util.Objects;

/**
 * A Builder for the GoogleMapsGeocoder
 */
public class GoogleMapsBuilder {

    private final GeoApiContext context;
    private boolean apiKeySet, cryptoSecretSet;
    private Locale lang;

    public GoogleMapsBuilder() {
        context = new GeoApiContext();
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
        context.setProxy(proxy);
        return this;
    }

    public GoogleMapsBuilder setApiKey(String apiKey) {
        context.setApiKey(apiKey);
        apiKeySet = Objects.nonNull(apiKey);
        return this;
    }

    public GoogleMapsBuilder setChannel(String channel) {
        context.setChannel(channel);
        return this;
    }

    public GoogleMapsBuilder setCredentials(String clientId, String cryptoSecret) {
        context.setEnterpriseCredentials(clientId, cryptoSecret);
        cryptoSecretSet = Objects.nonNull(cryptoSecret);
        return this;
    }

    public GoogleMapsGeocoder create() {
        // Check state
        if (cryptoSecretSet || apiKeySet) {
            return new GoogleMapsGeocoder(context, lang);
        } else {
            throw new IllegalStateException("Must provide either API key or Maps for Work credentials.");
        }
    }

}

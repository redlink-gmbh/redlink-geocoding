/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.google;

import com.google.maps.GeoApiContext;

import java.net.Proxy;
import java.util.Locale;

/**
 * A Builder for the GoogleMapsGeocoder
 */
public class GoogleMapsBuilder {

    private final GeoApiContext context;
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
        return this;
    }

    public GoogleMapsBuilder setChannel(String channel) {
        context.setChannel(channel);
        return this;
    }

    public GoogleMapsBuilder setCredentials(String clientId, String cryptoSecret) {
        context.setEnterpriseCredentials(clientId, cryptoSecret);
        return this;
    }

    public GoogleMapsGeocoder create() {
        return new GoogleMapsGeocoder(context, lang);
    }

}

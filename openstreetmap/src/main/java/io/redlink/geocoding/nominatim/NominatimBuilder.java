/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.nominatim;

import java.net.Proxy;
import java.util.Locale;

/**
 * A Builder for NominatimGeocoder
 */
public class NominatimBuilder {

    private String baseUrl;
    private String email = null;
    private Locale locale;
    private Proxy proxy = null;

    public NominatimBuilder() {
        baseUrl = NominatimGeocoder.PUBLIC_NOMINATIM_SERVER;
        locale = Locale.getDefault(Locale.Category.DISPLAY);
    }

    public NominatimGeocoder create() {
        return new NominatimGeocoder(baseUrl, locale, email, proxy);
    }

    public NominatimBuilder setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public NominatimBuilder setProxy(Proxy proxy) {
        this.proxy = proxy;
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
}

/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.nominatim;

import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.Locale;

/**
 * A Builder for NominatimGeocoder
 */
public class NominatimBuilder {

    private String baseUrl;
    private String email = null;
    private Locale locale;
    private Proxy proxy = null;
    private int maxQps = -1;

    public NominatimBuilder() {
        baseUrl = NominatimGeocoder.PUBLIC_NOMINATIM_SERVER;
        locale = Locale.getDefault(Locale.Category.DISPLAY);
    }

    public NominatimGeocoder create() {
        return new NominatimGeocoder(baseUrl, locale, email, proxy, maxQps);
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
}

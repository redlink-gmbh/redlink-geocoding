/*
 * Copyright (c) 2021 Redlink GmbH.
 */
package io.redlink.geocoding.proxy;

import java.net.URI;
import java.util.Locale;

public class ProxyBuilder {

    private URI baseUri;
    private Locale language;

    public ProxyBuilder() {
    }

    public ProxyBuilder(URI baseUri) {
        this.baseUri = baseUri;
    }

    public ProxyBuilder(String baseUri) {
        this(URI.create(baseUri));
    }

    public static ProxyBuilder configure() {
        return new ProxyBuilder();
    }

    public ProxyBuilder setBaseUri(URI baseUri) {
        this.baseUri = baseUri;
        return this;
    }

    public ProxyBuilder setBaseUri(String baseUri) {
        return setBaseUri(URI.create(baseUri));
    }

    public ProxyBuilder setLocale(Locale locale) {
        this.language = locale;
        return this;
    }

    public ProxyGeocoder create() {
        if (baseUri == null) {
            throw new IllegalStateException("baseUri must be set");
        } else if (!baseUri.isAbsolute()) {
            throw new IllegalArgumentException("baseUri must be absolute");
        }

        return new ProxyGeocoder(baseUri, language);
    }
}

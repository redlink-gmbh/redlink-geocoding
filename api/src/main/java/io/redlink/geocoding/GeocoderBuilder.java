/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding;

import java.net.Proxy;
import java.util.Locale;

/**
 */
public interface GeocoderBuilder<T extends  Geocoder> {

    GeocoderBuilder<T> setLocale(String language);

    GeocoderBuilder<T> setLocale(Locale locale);

    GeocoderBuilder<T> setProxy(Proxy proxy);

    GeocoderBuilder<T> setQueryRateLimit(int maxQps);

    T create();
}

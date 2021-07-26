/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.spring.boot.autoconfigure;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public abstract class GeocodingAutoConfiguration {

    protected final GeocodingProperties properties;

    protected GeocodingAutoConfiguration(GeocodingProperties properties) {
        this.properties = properties;
    }

    protected Proxy buildProxy() {
        final URL url = properties.getProxy();
        if (url == null) return Proxy.NO_PROXY;

        if ("http".equals(url.getProtocol())) {
            int port = url.getPort();
            if (port < 0) port = url.getDefaultPort();
            if (port < 0) port = 80;

            return new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(url.getHost(), port));
        } else {
            throw new IllegalArgumentException("Proxy-Protocol " + url.getProtocol() + " not supported.");
        }
    }
}

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
            if (port < 0) {
                port = url.getDefaultPort();
            }
            if (port < 0) {
                port = 80;
            }

            return new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(url.getHost(), port));
        } else {
            throw new IllegalArgumentException("Proxy-Protocol " + url.getProtocol() + " not supported.");
        }
    }
}

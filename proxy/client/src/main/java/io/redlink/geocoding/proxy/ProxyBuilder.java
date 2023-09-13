/*
 * Copyright (c) 2021-2022 Redlink GmbH.
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
package io.redlink.geocoding.proxy;

import java.net.URI;
import java.util.Locale;

public class ProxyBuilder {

    private URI baseUri;
    private Locale language;

    /**
     * @deprecated use {@link ProxyGeocoder#builder()}
     */
    @Deprecated(since = "2.0.2")
    public ProxyBuilder() {
    }

    /**
     * @deprecated use {@link ProxyGeocoder#builder()}
     */
    @Deprecated(since = "2.0.2")
    public ProxyBuilder(URI baseUri) {
        this.baseUri = baseUri;
    }

    /**
     * @deprecated use {@link ProxyGeocoder#builder()}
     */
    @Deprecated(since = "2.0.2")
    public ProxyBuilder(String baseUri) {
        this(URI.create(baseUri));
    }

    /**
     * @deprecated use {@link ProxyGeocoder#builder()}
     */
    @Deprecated(forRemoval = true, since = "1.1.0")
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

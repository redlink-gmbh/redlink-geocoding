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

    /**
     * @deprecated use {@link GoogleMapsGeocoder#builder()}
     */
    @Deprecated(since = "2.0.2")
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

    @SuppressWarnings("deprecation")
    public GoogleMapsGeocoder create() {
        // Check state
        if (cryptoSecretSet || apiKeySet) {
            return new GoogleMapsGeocoder(context.build(), lang, cryptoSecretSet, apiKeySet);
        } else {
            throw new IllegalStateException("Must provide either API key or Maps for Work credentials.");
        }
    }

}

/*
 * Copyright (c) 2022 Redlink GmbH.
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

import java.net.Proxy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
class GoogleMapsBuilderTest {

    private final Proxy proxy;

    public GoogleMapsBuilderTest() {
        proxy = mock(Proxy.class);
        when(proxy.type()).thenReturn(Proxy.Type.DIRECT);
    }

    @Test
    void testCreate() {
        assertNotNull(GoogleMapsGeocoder.builder()
                .setApiKey("API key")
                .setChannel("channel")
                .setCredentials("client", "cryptoSecret")
                .setLocale("de")
                .setProxy(proxy)
                .create(), "Create GoogleMaps instance");
    }

    @Test
    void testWithoutCredentials() {
        final GoogleMapsBuilder builder = GoogleMapsGeocoder.builder();
        assertThatCode(builder::create)
                .as("Incomplete Builder")
                .isInstanceOf(IllegalStateException.class);
    }
}

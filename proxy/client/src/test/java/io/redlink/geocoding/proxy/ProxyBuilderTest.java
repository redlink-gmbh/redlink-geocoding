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

package io.redlink.geocoding.proxy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProxyBuilderTest {

    @Test
    void testCreate() {
        assertNotNull(ProxyGeocoder.builder()
                .setBaseUri("https://example.com/")
                .create(), "Nominatim Builder");
    }

    @Test
    void testWithIllegalURI() {
        final ProxyBuilder builder = ProxyGeocoder.builder();
        assertThatCode(builder::create)
                .as("No baseUrl set")
                .isInstanceOf(IllegalStateException.class);

        builder.setBaseUri("/foo/bar");
        assertThatCode(builder::create)
                .as("Relative baseUrl set")
                .isInstanceOf(IllegalArgumentException.class);
    }

}
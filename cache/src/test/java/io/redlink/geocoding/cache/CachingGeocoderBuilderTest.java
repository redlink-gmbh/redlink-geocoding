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

package io.redlink.geocoding.cache;

import io.redlink.geocoding.Geocoder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
class CachingGeocoderBuilderTest {

    @Test
    void testCreateWithoutDelegate() {
        final CachingGeocoderBuilder builder = CachingGeocoder.builder();
        assertThatCode(builder::create)
                .as("Incomplete Builder")
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testCreate() {
        final Geocoder delegate = Mockito.mock(Geocoder.class);
        assertNotNull(CachingGeocoder.builder()
                .setGeocoder(delegate)
                .create(), "Caching Builder");
        assertNotNull(CachingGeocoder.wrap(delegate)
                .create(), "Caching Builder");
    }

}
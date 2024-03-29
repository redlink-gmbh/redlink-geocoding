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

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.proxy.ProxyGeocoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("proxy")
@EnableAutoConfiguration
class ProxyGeocoderTest {
    // NOTE: see https://docs.spring.io/spring-boot/docs/2.7.15/reference/htmlsingle/#features.developing-auto-configuration.testing
    //       on how to properly test autoconfiguration

    private final Geocoder geocoder;

    @Autowired
    ProxyGeocoderTest(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    @Test
    void testInject() {
        assertNotNull(geocoder, "Geocoder-Bean missing");
    }

    @Test
    void testType() {
        assertThat(geocoder)
                .as("ProxyGeocoderTest expected")
                .isInstanceOf(ProxyGeocoder.class);
    }
}

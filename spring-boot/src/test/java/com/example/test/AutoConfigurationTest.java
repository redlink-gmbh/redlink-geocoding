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
package com.example.test;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import io.redlink.geocoding.cache.CachingGeocoder;
import io.redlink.geocoding.google.GoogleMapsGeocoder;
import io.redlink.geocoding.nominatim.NominatimGeocoder;
import io.redlink.geocoding.proxy.ProxyGeocoder;
import io.redlink.geocoding.spring.boot.autoconfigure.CachedGeocodingAutoconfiguration;
import io.redlink.geocoding.spring.boot.autoconfigure.GoogleGeocodingAutoConfiguration;
import io.redlink.geocoding.spring.boot.autoconfigure.NominatimGeocodingAutoConfiguration;
import io.redlink.geocoding.spring.boot.autoconfigure.ProxyGeocodingAutoConfiguration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * see https://docs.spring.io/spring-boot/docs/2.7.15/reference/htmlsingle/#features.developing-auto-configuration.testing
 */
class AutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    NominatimGeocodingAutoConfiguration.class,
                    GoogleGeocodingAutoConfiguration.class,
                    CachedGeocodingAutoconfiguration.class,
                    ProxyGeocodingAutoConfiguration.class
            ));

    @Test
    void testCachedWithGoogleBackend() {
        contextRunner
                .withPropertyValues(
                        "geocoding.cache-timeout=3600",
                        "geocoding.google.apiKey=your-google-api-key"
                )
                .run(context ->
                        assertThat(context.getBean(Geocoder.class)).isInstanceOf(CachingGeocoder.class)
                );
    }

    @Test
    void testDefaultGeocoderNominatim() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(Geocoder.class);
                    assertThat(context.getBean(Geocoder.class)).isInstanceOf(NominatimGeocoder.class);
                });
    }

    @Test
    void testGoogleGeocoder() {
        contextRunner
                .withPropertyValues("geocoding.google.api-key=AIza-this-is-an-invalid-key")
                .run(context -> {
                    assertThat(context).hasSingleBean(Geocoder.class);
                    assertThat(context.getBean(Geocoder.class)).isInstanceOf(GoogleMapsGeocoder.class);
                });
    }

    @Test
    void testProxyGeocoder() {
        contextRunner
                .withPropertyValues("geocoding.proxy-service.base-url=http://localhost:9123/api")
                .run(context -> {
                    assertThat(context).hasSingleBean(Geocoder.class);
                    assertThat(context.getBean(Geocoder.class)).isInstanceOf(ProxyGeocoder.class);
                });
    }

    @Test
    void testGeocoderPriority() {
        contextRunner
                .withPropertyValues(
                        "geocoding.google.api-key = AIza-this-is-an-invalid-key",
                        "geocoding.proxy-service.base-url = http://localhost:9123/api",
                        "geocoding.nominatim.base-url = https://www.example.com"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(GoogleMapsGeocoder.class);
                    assertThat(context).doesNotHaveBean(NominatimGeocoder.class);
                    assertThat(context).doesNotHaveBean(ProxyGeocoder.class);
                    assertThat(context.getBean(Geocoder.class)).isInstanceOf(GoogleMapsGeocoder.class);
                });
    }

    @Test
    void defaultServiceBacksOff() {
        this.contextRunner
                .withUserConfiguration(CustomConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(Geocoder.class);
                    assertThat(context).getBean("customGeocoder").isSameAs(context.getBean(Geocoder.class));
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomConfiguration {

        @Bean
        Geocoder customGeocoder() {
            return new Geocoder() {
                @Override
                public List<Place> geocode(String address, Locale lang) {
                    return List.of();
                }

                @Override
                public List<Place> reverseGeocode(LatLon coordinates, Locale lang) {
                    return List.of();
                }

                @Override
                public Optional<Place> lookup(String placeId, Locale lang) {
                    return Optional.empty();
                }
            };
        }
    }
}

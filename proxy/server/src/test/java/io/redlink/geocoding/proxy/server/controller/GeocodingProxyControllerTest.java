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

package io.redlink.geocoding.proxy.server.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import io.redlink.geocoding.proxy.io.PlaceDTO;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeocodingProxyControllerTest {

    private final GeocodingProxyController controller;

    private final Locale lang = Locale.GERMAN;

    GeocodingProxyControllerTest() {
        controller = new GeocodingProxyController(
                geocoder(), meterRegistry()
        );
    }


    @Test
    void testGeocode() {
        final ResponseEntity<List<PlaceDTO>> response2 = controller.geocode("Salzburg", lang);
        assertTrue(response2.getStatusCode().is2xxSuccessful(), "Status Code 200");
        assertTrue(response2.hasBody(), "Body Expected");
        assertThat(response2.getBody())
                .describedAs("Validate Geocoding Response")
                .singleElement()
                .hasFieldOrPropertyWithValue("placeId", "place-1")
                .hasFieldOrPropertyWithValue("address", "Salzburg")
        ;
    }

    @Test
    void testReverseGeocode() {
        final ResponseEntity<List<PlaceDTO>> response2 = controller.reverseGeocode(45,45, lang);
        assertTrue(response2.getStatusCode().is2xxSuccessful(), "Status Code 200");
        assertTrue(response2.hasBody(), "Body Expected");
        assertThat(response2.getBody())
                .describedAs("Validate Reverse-Geocoding Response")
                .singleElement()
                .hasFieldOrPropertyWithValue("placeId", "place-2")
                .hasFieldOrPropertyWithValue("address", "Somewhere")
        ;
    }

    @Test
    void testLookup() {
        final ResponseEntity<PlaceDTO> response2 = controller.lookup("place-999", lang);
        assertTrue(response2.getStatusCode().is2xxSuccessful(), "Status Code 200");
        assertTrue(response2.hasBody(), "Body Expected");
        assertThat(response2.getBody())
                .describedAs("Validate Lookup Response")
                .hasFieldOrPropertyWithValue("placeId", "place-999")
                .hasFieldOrPropertyWithValue("address", "Somewhere")
        ;
    }



    private static Geocoder geocoder() {
        return new Geocoder() {
            @Override
            public List<Place> geocode(String address, Locale lang) {
                return List.of(Place.create(
                        "place-1", address, LatLon.create(15, 15)
                ));
            }

            @Override
            public List<Place> reverseGeocode(LatLon coordinates, Locale lang) {
                return List.of(Place.create(
                        "place-2", "Somewhere", coordinates
                ));
            }

            @Override
            public Optional<Place> lookup(String placeId, Locale lang) {
                return Optional.of(Place.create(
                        placeId, "Somewhere", LatLon.create(15, 15)
                ));
            }
        };
    }

    private static MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

}
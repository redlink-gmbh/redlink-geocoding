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

package io.redlink.geocoding.nominatim;

import io.redlink.geocoding.AddressComponent;
import io.redlink.geocoding.AddressComponent.Type;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.Assumptions.assumeThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 */
class NominatimGeocoderIT {

    private final String testPlaceId = "W30514164";
    private final String testFormattedAddress = "Techno-Z III, Jakob-Haringer-Straße, Techno-Z, Itzling, Salzburg, 5020, Austria";
    private final String coworkingFormattedAddress = "Coworkingspace Salzburg, 3, Jakob-Haringer-Straße, Techno-Z, Itzling, Salzburg, 5020, Austria";
    private final String testAddress = "jakob haringer strasse 3";
    private final double testLat = 47.8227343;
    private final double testLon = 13.0408988;

    private final LatLon latLon;

    private final NominatimGeocoder osmGeocoder;

    public NominatimGeocoderIT() {
        latLon = LatLon.create(testLat, testLon);

        osmGeocoder = new NominatimGeocoder(NominatimGeocoder.PUBLIC_NOMINATIM_SERVER,
                Locale.forLanguageTag("en"),
                System.getProperty("nominatim.email"),
                null);
    }

    @BeforeEach
    public void pingRemote() {
        assumeThatCode(() -> {
            try (CloseableHttpClient client = HttpClients.createDefault()) {

                final int status = client.execute(new HttpHead(NominatimGeocoder.PUBLIC_NOMINATIM_SERVER), ClassicHttpResponse::getCode);
                assumeThat(status)
                        .as("Remote Service Status")
                        .isBetween(200, 299);
            }
        })
        .doesNotThrowAnyException();
    }

    @Test
    void testGeocode() throws IOException {
        final List<Place> places = osmGeocoder.geocode(testAddress);

        Assertions.assertThat(places)
                .as("OSM Places")
                .hasSize(2)
                .as("First Result")
                .first()
                .hasFieldOrPropertyWithValue("placeId", "N3081433444")
                .hasFieldOrPropertyWithValue("address", coworkingFormattedAddress);


        Collection<AddressComponent> addrComps = places.get(0).getComponents();

        EnumMap<Type, String> expected = new EnumMap<>(Type.class);
        expected.put(Type.streetNumber, "3");
        expected.put(Type.street, "Jakob-Haringer-Straße");
        expected.put(Type.city, "Salzburg");
        expected.put(Type.postalCode, "5020");
        expected.put(Type.state, "Salzburg");
        expected.put(Type.countryCode, "at");
        expected.put(Type.country, "Austria");
        for(AddressComponent ac : addrComps){
            String expValue = expected.remove(ac.getType());
            assertNotNull(expValue, "Unexpected " + ac);
            assertEquals(expValue, ac.getValue(), "AddressComponent");
        }

        assumeThat(expected)
                .as("Missing expected AddressComponents "+ expected)
                .isEmpty();
    }

    @Test
    void testReverseGeocode() throws IOException {
        final List<Place> places = osmGeocoder.reverseGeocode(latLon);

        Assertions.assertThat(places)
                .as("reverse geocoding results")
                .singleElement()
                .as("reverse geocoded place")
                .hasFieldOrPropertyWithValue("placeId", testPlaceId)
                .hasFieldOrPropertyWithValue("address", testFormattedAddress);
    }

    @Test
    void testLookup() throws IOException {
        final Optional<Place> place = osmGeocoder.lookup(testPlaceId);

        Assertions.assertThat(place)
                .as("place lookup")
                .isPresent().get()
                .hasFieldOrPropertyWithValue("placeId", testPlaceId)
                .hasFieldOrPropertyWithValue("address", testFormattedAddress);
    }

    @Test
    void testInvalidLookup() throws IOException {
        Assertions.assertThat(osmGeocoder.lookup("#!invalid"))
                .as("Invalid placeId -> empty")
                .isEmpty();
    }
}

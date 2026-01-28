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

import com.google.maps.internal.ApiConfig;
import io.redlink.geocoding.AddressComponent;
import io.redlink.geocoding.AddressComponent.Type;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.Assumptions.assumeThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
class GoogleMapsGeocoderIT {

    static {
        LoggerFactory.getLogger(GoogleMapsGeocoderIT.class)
                .debug("Enable jul-to-slf4j bridge");
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private static final String TEST_PLACE_ID = "ChIJz0qJMpqadkcRpaXIPyX0sI8";
    private static final String TEST_ADDRESS = "jakob haringer strasse 3";
    private static final Double TEST_LAT = 47.82273;
    private static final Double TEST_LON = 13.040612;

    private final GoogleMapsGeocoder gmGeocoder;

    private final LatLon latLon = LatLon.create(TEST_LAT, TEST_LON);

    public GoogleMapsGeocoderIT() {
        final String apiKey = System.getProperty("google.apiKey", System.getenv("GOOGLE_API_KEY"));

        assumeThat(apiKey)
                .as("Google API-Key missing, provide it with -Dgoogle.apiKey")
                .isNotEmpty()
                .as("Invalid Google API-Key (expected to start with 'AIza'")
                .startsWith("AIza");

        gmGeocoder = GoogleMapsGeocoder.builder()
                .setApiKey(apiKey)
                .setChannel(getClass().getSimpleName())
                .setLocale("en")
                .create();
    }

    @BeforeEach
    void pingRemote() {
        assumeThatCode(() -> {
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest request = HttpRequest.newBuilder()
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .uri(URI.create(new ApiConfig("/").hostName))
                    .build();
            final HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            assumeThat(response.statusCode())
                    .as("Ping to remote service")
                    .isBetween(200, 399);
        })
                .as("Ping to remote service")
                .doesNotThrowAnyException();
    }

    @Test
    void testGeocode() throws IOException {
        final List<Place> places = gmGeocoder.geocode(TEST_ADDRESS);

        assertThat(places)
                .as("Geocoded Place")
                .isNotEmpty()
                .first()
                .hasFieldOrProperty("placeId")
                .hasFieldOrProperty("address");

        Collection<AddressComponent> addrComps = places.get(0).getComponents();

        EnumMap<Type, String> expected = new EnumMap<>(Type.class);
        expected.put(Type.streetNumber, "3");
        expected.put(Type.street, "Jakob-Haringer-Straße");
        expected.put(Type.sublocality, "Itzling");
        expected.put(Type.city, "Salzburg");
        expected.put(Type.postalCode, "5020");
        expected.put(Type.state, "Salzburg");
        expected.put(Type.countryCode, "at");
        expected.put(Type.country, "Austria");
        for (AddressComponent ac : addrComps) {
            String expValue = expected.remove(ac.getType());
            assertNotNull(expValue, "Unexpected " + ac);
            assertEquals(expValue, ac.getValue(), "AddressComponent");
        }
        assertThat(expected)
                .as("Missing expected AddressComponents " + expected)
                .isEmpty();

    }

    @Test
    void testReverseGeocode() throws IOException {
        final List<Place> places = gmGeocoder.reverseGeocode(latLon);

        assertThat(places)
                .as("places found")
                .hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void testLookup() throws IOException {
        final Optional<Place> place = gmGeocoder.lookup(TEST_PLACE_ID);

        assertThat(place)
                .as("Place Lookup")
                .isPresent().get()
                .hasFieldOrPropertyWithValue("placeId", TEST_PLACE_ID)
                .hasFieldOrProperty("address");
    }

    @Test
    void testInvalidLookup() throws IOException {
        assertThat(gmGeocoder.lookup("#!invalid"))
                .as("Invalid placeId -> empty()")
                .isEmpty();

    }
}

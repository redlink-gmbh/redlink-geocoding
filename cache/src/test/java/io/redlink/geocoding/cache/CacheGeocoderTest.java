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
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;

/**
 *
 */
@SuppressWarnings("java:S2925")
class CacheGeocoderTest {

    public static final String TEST_ADDRESS = "test address";
    public static final String TEST_ID = "test ID";
    public static final double TEST_LAT = 45.0;
    public static final double TEST_LON = 12.0;

    private final LatLon mockLatLon = LatLon.valueOf(String.format("%f,%f", TEST_LAT, TEST_LON));
    private final Place mockPlace = Place.create(TEST_ID, TEST_ADDRESS, mockLatLon);

    private final CachingGeocoder geocoder;

    public CacheGeocoderTest() {
        geocoder = CachingGeocoder.wrap(new MockGeocoder(mockPlace)).create();
    }

    @Test
    void testGeocode() throws IOException {
        List<Place> places = geocoder.geocode("any address");

        assertThat(places)
                .as("singe geocoding result")
                .singleElement()
                .isEqualTo(mockPlace);
    }

    @Test
    void testReverseGeocode() throws IOException {
        List<Place> places = geocoder.reverseGeocode(mockLatLon);
        assertThat(places)
                .as("singe reverse-geocoding result")
                .singleElement()
                .isEqualTo(mockPlace);


    }

    @Test
    void testLookup() throws IOException {
        Optional<Place> place = geocoder.lookup("any address");

        assertThat(place)
                .as("lookup result")
                .isPresent()
                .hasValue(mockPlace);
    }

    @Test
    void testLookupCaching() throws Exception {
        final String placeId_1 = UUID.randomUUID().toString(), placeId_2 = UUID.randomUUID().toString();

        final Geocoder delegate = Mockito.mock(Geocoder.class);
        Mockito.when(delegate.lookup(placeId_1, (Locale) null))
                .thenReturn(Optional.of(Place.create(placeId_1, null, null)));
        Mockito.when(delegate.lookup(placeId_2, (Locale) null))
                .thenReturn(Optional.of(Place.create(placeId_2, null, null)));

        final CachingGeocoder cache = CachingGeocoder.wrap(delegate)
                .setCacheExpiry(2, TimeUnit.SECONDS)
                .create();
        assertThat(cache.lookup(placeId_1))
                .as("lookup place 1")
                .isPresent().get()
                .hasFieldOrPropertyWithValue("placeId", placeId_1);
        assertThat(cache.lookup(placeId_1))
                .as("lookup place 1 again")
                .isPresent().get()
                .hasFieldOrPropertyWithValue("placeId", placeId_1);

        TimeUnit.SECONDS.sleep(1);
        assertThat(cache.lookup(placeId_2))
                .as("lookup place 2")
                .isPresent().get()
                .hasFieldOrPropertyWithValue("placeId", placeId_2);
        assertThat(cache.lookup(placeId_1))
                .as("lookup place 1 again")
                .isPresent().get()
                .hasFieldOrPropertyWithValue("placeId", placeId_1);

        TimeUnit.SECONDS.sleep(1);
        assertThat(cache.lookup(placeId_2))
                .as("lookup place 2 again")
                .isPresent().get()
                .hasFieldOrPropertyWithValue("placeId", placeId_2);
        assertThat(cache.lookup(placeId_1))
                .as("lookup place 1 again")
                .isPresent().get()
                .hasFieldOrPropertyWithValue("placeId", placeId_1);

        Mockito.verify(delegate, Mockito.never()).geocode(Mockito.anyString());
        Mockito.verify(delegate, Mockito.never()).reverseGeocode(Mockito.any(LatLon.class));
        Mockito.verify(delegate, Mockito.times(2)).lookup(placeId_1, (Locale) null);
        Mockito.verify(delegate, Mockito.times(1)).lookup(placeId_2, (Locale) null);

    }

    @Test
    void testGeocodeCaching() throws Exception {
        final String placeId_1 = UUID.randomUUID().toString(),
                placeId_2 = UUID.randomUUID().toString();
        final Place place_1 = Place.create(placeId_1, "place1", LatLon.create(1, 1)),
                place_2 = Place.create(placeId_2, "place2", LatLon.create(2, 2));

        final Geocoder delegate = Mockito.mock(Geocoder.class);
        Mockito.when(delegate.geocode(placeId_1, (Locale) null))
                .thenReturn(Collections.singletonList(place_1));
        Mockito.when(delegate.geocode(placeId_2, (Locale) null))
                .thenReturn(Collections.singletonList(place_2));

        final CachingGeocoder cache = CachingGeocoder.wrap(delegate)
                .setCacheExpiry(2, TimeUnit.SECONDS)
                .create();
        assertThat(cache.geocode(placeId_1, (Locale) null))
                .as("geocode place1")
                .singleElement()
                .isEqualTo(place_1);
        assertThat(cache.geocode(placeId_1))
                .as("geocode place1 again")
                .singleElement()
                .isEqualTo(place_1);

        TimeUnit.SECONDS.sleep(1);
        assertThat(cache.geocode(placeId_1))
                .as("geocode place1 again")
                .singleElement()
                .isEqualTo(place_1);
        assertThat(cache.geocode(placeId_2))
                .as("geocode place2 again")
                .singleElement()
                .isEqualTo(place_2);

        TimeUnit.SECONDS.sleep(1);
        assertThat(cache.geocode(placeId_2))
                .as("geocode place2 again")
                .singleElement()
                .isEqualTo(place_2);
        assertThat(cache.geocode(placeId_1))
                .as("geocode place1 again")
                .singleElement()
                .isEqualTo(place_1);

        Mockito.verify(delegate, Mockito.times(2)).geocode(placeId_1, (Locale) null);
        Mockito.verify(delegate, Mockito.times(1)).geocode(placeId_2, (Locale) null);
        Mockito.verify(delegate, Mockito.never()).reverseGeocode(Mockito.any(LatLon.class), anyString());
        Mockito.verify(delegate, Mockito.never()).lookup(Mockito.anyString(), anyString());
    }

    @Test
    void testReverseCaching() throws Exception {
        final Random rnd = new Random();
        final LatLon loc_1 = LatLon.create(-90d + 180d * rnd.nextDouble(), -180d + 360d * rnd.nextDouble()),
                loc_2 = LatLon.create(-90d + 180d * rnd.nextDouble(), -180d + 360d * rnd.nextDouble());
        final String placeId_1 = UUID.randomUUID().toString(), placeId_2 = UUID.randomUUID().toString();
        final Place place_1 = Place.create(placeId_1, null, loc_1),
                place_2 = Place.create(placeId_2, null, loc_2);

        final Geocoder delegate = Mockito.mock(Geocoder.class);
        Mockito.when(delegate.reverseGeocode(loc_1, (Locale) null)).thenReturn(Collections.singletonList(place_1));
        Mockito.when(delegate.reverseGeocode(loc_2, (Locale) null)).thenReturn(Collections.singletonList(place_2));

        final CachingGeocoder cache = CachingGeocoder.wrap(delegate)
                .setCacheExpiry(2, TimeUnit.SECONDS)
                .create();
        assertThat(cache.reverseGeocode(loc_1))
                .as("reverse geocode place1")
                .singleElement()
                .isEqualTo(place_1);
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_1, (Locale) null);
        assertThat(cache.reverseGeocode(loc_1))
                .as("reverse geocode place1 again")
                .singleElement()
                .isEqualTo(place_1);
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_1, (Locale) null);

        TimeUnit.SECONDS.sleep(1);
        assertThat(cache.reverseGeocode(loc_1))
                .as("reverse geocode place1 again")
                .singleElement()
                .isEqualTo(place_1);
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_1, (Locale) null);
        assertThat(cache.reverseGeocode(loc_2))
                .as("reverse geocode place2")
                .singleElement()
                .isEqualTo(place_2);
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_2, (Locale) null);

        TimeUnit.SECONDS.sleep(1);
        assertThat(cache.reverseGeocode(loc_2))
                .as("reverse geocode place2 again")
                .singleElement()
                .isEqualTo(place_2);
        assertThat(cache.reverseGeocode(loc_1))
                .as("reverse geocode place1 again")
                .singleElement()
                .isEqualTo(place_1);

        Mockito.verify(delegate, Mockito.never()).geocode(Mockito.anyString());
        Mockito.verify(delegate, Mockito.times(2)).reverseGeocode(loc_1, (Locale) null);
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_2, (Locale) null);
        Mockito.verify(delegate, Mockito.never()).lookup(Mockito.anyString());
    }


    private static class MockGeocoder implements Geocoder {

        private final Place mockPlace;

        private MockGeocoder(Place mockPlace) {
            this.mockPlace = mockPlace;
        }

        @Override
        public List<Place> geocode(String address, Locale lang) {
            return Collections.singletonList(mockPlace);
        }

        @Override
        public List<Place> reverseGeocode(LatLon coordinates, Locale lang) {
            return Collections.singletonList(mockPlace);
        }

        @Override
        public Optional<Place> lookup(String placeId, Locale lang) {
            return Optional.of(mockPlace);
        }
    }

}

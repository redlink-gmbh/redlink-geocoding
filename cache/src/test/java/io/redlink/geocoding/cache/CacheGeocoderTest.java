package io.redlink.geocoding.cache;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.anyString;

/**
 *
 */
public class CacheGeocoderTest {

    public static final String TEST_ADDRESS = "test address";
    public static final String TEST_ID = "test ID";
    public static final double TEST_LAT = 45.0;
    public static final double TEST_LON = 12.0;

    private final LatLon mockLatLon = LatLon.valueOf(String.format("%f,%f", TEST_LAT, TEST_LON));
    private final Place mockPlace = Place.create(TEST_ID, TEST_ADDRESS, mockLatLon);

    private final CachingGeocoder geocoder;

    public CacheGeocoderTest() {
        geocoder = new CachingGeocoder(new MockGeocoder(mockPlace));
    }

    @Test
    public void testGeocode() throws IOException {
        List<Place> places = geocoder.geocode("any address");
        Assert.assertEquals(1, places.size());
        Assert.assertEquals(TEST_ID, places.get(0).getPlaceId());
        Assert.assertEquals(TEST_ADDRESS, places.get(0).getAddress());
        Assert.assertEquals(TEST_LAT, places.get(0).getLatLon().lat(), 0);
        Assert.assertEquals(TEST_LON, places.get(0).getLatLon().lon(), 0);
    }

    @Test
    public void testReverseGeocode() throws IOException {
        List<Place> places = geocoder.reverseGeocode(mockLatLon);
        Assert.assertEquals(1, places.size());
        Assert.assertEquals(TEST_ID, places.get(0).getPlaceId());
        Assert.assertEquals(TEST_ADDRESS, places.get(0).getAddress());
        Assert.assertEquals(TEST_LAT, places.get(0).getLatLon().lat(), 0);
        Assert.assertEquals(TEST_LON, places.get(0).getLatLon().lon(), 0);
    }

    @Test
    public void testLookup() throws IOException {
        Place place = geocoder.lookup("any address");
        Assert.assertEquals(TEST_ID, place.getPlaceId());
        Assert.assertEquals(TEST_ADDRESS, place.getAddress());
        Assert.assertEquals(TEST_LAT, place.getLatLon().lat(), 0);
        Assert.assertEquals(TEST_LON, place.getLatLon().lon(), 0);
    }

    @Test
    public void testLookupCaching() throws Exception {
        final String placeId_1 = UUID.randomUUID().toString(), placeId_2 = UUID.randomUUID().toString();

        final Geocoder delegate = Mockito.mock(Geocoder.class);
        Mockito.when(delegate.lookup(placeId_1, (Locale) null))
                .thenReturn(Place.create(placeId_1, null, null));
        Mockito.when(delegate.lookup(placeId_2, (Locale) null))
                .thenReturn(Place.create(placeId_2, null, null));

        final CachingGeocoder cache = new CachingGeocoder(delegate, 2, TimeUnit.SECONDS);
        assertEquals(cache.lookup(placeId_1).getPlaceId(), placeId_1);
        assertEquals(cache.lookup(placeId_1).getPlaceId(), placeId_1);

        TimeUnit.SECONDS.sleep(1);
        assertEquals(cache.lookup(placeId_2).getPlaceId(), placeId_2);
        assertEquals(cache.lookup(placeId_1).getPlaceId(), placeId_1);

        TimeUnit.SECONDS.sleep(1);
        assertEquals(cache.lookup(placeId_2).getPlaceId(), placeId_2);
        assertEquals(cache.lookup(placeId_1).getPlaceId(), placeId_1);

        Mockito.verify(delegate, Mockito.never()).geocode(Mockito.anyString());
        Mockito.verify(delegate, Mockito.never()).reverseGeocode(Mockito.any(LatLon.class));
        Mockito.verify(delegate, Mockito.times(2)).lookup(placeId_1, (Locale) null);
        Mockito.verify(delegate, Mockito.times(1)).lookup(placeId_2, (Locale) null);

    }

    @Test
    public void testGeocodeCaching() throws Exception {
        final String placeId_1 = UUID.randomUUID().toString(),
                placeId_2 = UUID.randomUUID().toString();
        final Place place_1 = Place.create(placeId_1, "place1", new LatLon(1, 1)),
                place_2 = Place.create(placeId_2, "place2", new LatLon(2, 2));

        final Geocoder delegate = Mockito.mock(Geocoder.class);
        Mockito.when(delegate.geocode(placeId_1, (Locale) null))
                .thenReturn(Collections.singletonList(place_1));
        Mockito.when(delegate.geocode(placeId_2, (Locale) null))
                .thenReturn(Collections.singletonList(place_2));

        final CachingGeocoder cache = new CachingGeocoder(delegate, 2, TimeUnit.SECONDS);
        assertThat(cache.geocode(placeId_1, (Locale) null), Matchers.contains(place_1));
        assertThat(cache.geocode(placeId_1), Matchers.contains(place_1));

        TimeUnit.SECONDS.sleep(1);
        assertThat(cache.geocode(placeId_1), Matchers.contains(place_1));
        assertThat(cache.geocode(placeId_2), Matchers.contains(place_2));

        TimeUnit.SECONDS.sleep(1);
        assertThat(cache.geocode(placeId_2), Matchers.contains(place_2));
        assertThat(cache.geocode(placeId_1), Matchers.contains(place_1));

        Mockito.verify(delegate, Mockito.times(2)).geocode(placeId_1, (Locale) null);
        Mockito.verify(delegate, Mockito.times(1)).geocode(placeId_2, (Locale) null);
        Mockito.verify(delegate, Mockito.never()).reverseGeocode(Mockito.any(LatLon.class), anyString());
        Mockito.verify(delegate, Mockito.never()).lookup(Mockito.anyString(), anyString());
    }

    @Test
    public void testReverseCaching() throws Exception {
        final Random rnd = new Random();
        final LatLon loc_1 = new LatLon(-90d + 180d * rnd.nextDouble(), -180d + 360d * rnd.nextDouble()),
                loc_2 = new LatLon(-90d + 180d * rnd.nextDouble(), -180d + 360d * rnd.nextDouble());
        final String placeId_1 = UUID.randomUUID().toString(), placeId_2 = UUID.randomUUID().toString();
        final Place place_1 = Place.create(placeId_1, null, loc_1),
                place_2 = Place.create(placeId_2, null, loc_2);

        final Geocoder delegate = Mockito.mock(Geocoder.class);
        Mockito.when(delegate.reverseGeocode(loc_1, (Locale) null)).thenReturn(Collections.singletonList(place_1));
        Mockito.when(delegate.reverseGeocode(loc_2, (Locale) null)).thenReturn(Collections.singletonList(place_2));

        final CachingGeocoder cache = new CachingGeocoder(delegate, 2, TimeUnit.SECONDS);
        assertThat(cache.reverseGeocode(loc_1), Matchers.contains(place_1));
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_1, (Locale) null);
        assertThat(cache.reverseGeocode(loc_1), Matchers.contains(place_1));
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_1, (Locale) null);

        TimeUnit.SECONDS.sleep(1);
        assertThat(cache.reverseGeocode(loc_1), Matchers.contains(place_1));
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_1, (Locale) null);
        assertThat(cache.reverseGeocode(loc_2), Matchers.contains(place_2));
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_2, (Locale) null);

        TimeUnit.SECONDS.sleep(1);
        assertThat(cache.reverseGeocode(loc_2), Matchers.contains(place_2));
        assertThat(cache.reverseGeocode(loc_1), Matchers.contains(place_1));

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
        public Place lookup(String placeId, Locale lang) {
            return mockPlace;
        }
    }

}

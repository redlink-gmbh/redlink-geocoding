package io.redlink.geocoding.cache;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 */
public class CacheGeocoderTest {

    public static final String TEST_ADDRESS = "test address";
    public static final String TEST_ID = "test ID";
    public static final double TEST_LAT = 45.0;
    public static final double TEST_LON = 12.0;
    @Mock
    private Geocoder mockGeocoder;
    @Mock
    private Place mockPlace;
    @Mock
    private LatLon mockLatLon;

    private final CacheGeocoder geocoder;

    public CacheGeocoderTest() {
        MockitoAnnotations.initMocks(this);
        geocoder = new CacheGeocoder(mockGeocoder);
    }

    @Before
    public void init() throws IOException {
        //Mocking geocoder
        when(mockGeocoder.geocode(anyString())).thenReturn(Collections.singletonList(mockPlace));
        when(mockGeocoder.reverseGeocode(any(LatLon.class))).thenReturn(Collections.singletonList(mockPlace));
        when(mockGeocoder.lookup(anyString())).thenReturn(mockPlace);

        //Mocking place
        when(mockPlace.getPlaceId()).thenReturn(TEST_ID);
        when(mockPlace.getAddress()).thenReturn(TEST_ADDRESS);
        when(mockPlace.getLatLon()).thenReturn(mockLatLon);

        //Mocking latlon
        when(mockLatLon.lat()).thenReturn(TEST_LAT);
        when(mockLatLon.lon()).thenReturn(TEST_LON);
    }

    @Test
    public void testGeocode() throws IOException {
        List<Place> places = geocoder.geocode("any address");
        Assert.assertEquals(1, places.size());
        Assert.assertEquals(TEST_ID, places.get(0).getPlaceId());
        Assert.assertEquals(TEST_ADDRESS, places.get(0).getAddress());
        Assert.assertEquals(TEST_LAT, places.get(0).getLatLon().lat(),0);
        Assert.assertEquals(TEST_LON, places.get(0).getLatLon().lon(),0);
    }

    @Test
    public void testReverseGeocode() throws IOException {
        List<Place> places = geocoder.reverseGeocode(mockLatLon);
        Assert.assertEquals(1, places.size());
        Assert.assertEquals(TEST_ID, places.get(0).getPlaceId());
        Assert.assertEquals(TEST_ADDRESS, places.get(0).getAddress());
        Assert.assertEquals(TEST_LAT, places.get(0).getLatLon().lat(),0);
        Assert.assertEquals(TEST_LON, places.get(0).getLatLon().lon(),0);
    }

    @Test
    public void testLookup() throws IOException {
        Place place = geocoder.lookup("any address");
        Assert.assertEquals(TEST_ID, place.getPlaceId());
        Assert.assertEquals(TEST_ADDRESS, place.getAddress());
        Assert.assertEquals(TEST_LAT, place.getLatLon().lat(),0);
        Assert.assertEquals(TEST_LON, place.getLatLon().lon(),0);
    }

    @Test
    public void testLookupCaching() throws Exception {
        final String placeId_1 = UUID.randomUUID().toString(), placeId_2 = UUID.randomUUID().toString();

        final Geocoder delegate = Mockito.mock(Geocoder.class);
        Mockito.when(delegate.lookup(Mockito.anyString()))
                .thenAnswer(invocation -> new Place(String.valueOf(invocation.getArguments()[0])))  ;

        final CacheGeocoder cache = new CacheGeocoder(delegate, 2, TimeUnit.SECONDS);
        assertEquals(cache.lookup(placeId_1).getPlaceId(), placeId_1);
        assertEquals(cache.lookup(placeId_1).getPlaceId(), placeId_1);

        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        assertEquals(cache.lookup(placeId_2).getPlaceId(), placeId_2);
        assertEquals(cache.lookup(placeId_1).getPlaceId(), placeId_1);

        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        assertEquals(cache.lookup(placeId_2).getPlaceId(), placeId_2);
        assertEquals(cache.lookup(placeId_1).getPlaceId(), placeId_1);

        Mockito.verify(delegate, Mockito.never()).geocode(Mockito.anyString());
        Mockito.verify(delegate, Mockito.never()).reverseGeocode(Mockito.any(LatLon.class));
        Mockito.verify(delegate, Mockito.times(2)).lookup(placeId_1);
        Mockito.verify(delegate, Mockito.times(1)).lookup(placeId_2);

    }

    @Test
    public void testGeocodeCaching() throws Exception {
        final String placeId_1 = UUID.randomUUID().toString(), placeId_2 = UUID.randomUUID().toString();
        final Place place_1 = new Place(placeId_1), place_2 = new Place(placeId_2);

        final Geocoder delegate = Mockito.mock(Geocoder.class);
        Mockito.when(delegate.geocode(placeId_1)).thenReturn(Collections.singletonList(place_1));
        Mockito.when(delegate.geocode(placeId_2)).thenReturn(Collections.singletonList(place_2));

        final CacheGeocoder cache = new CacheGeocoder(delegate, 2, TimeUnit.SECONDS);
        assertThat(cache.geocode(placeId_1), Matchers.contains(place_1));
        assertThat(cache.geocode(placeId_1), Matchers.contains(place_1));

        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        assertThat(cache.geocode(placeId_1), Matchers.contains(place_1));
        assertThat(cache.geocode(placeId_2), Matchers.contains(place_2));

        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        assertThat(cache.geocode(placeId_2), Matchers.contains(place_2));
        assertThat(cache.geocode(placeId_1), Matchers.contains(place_1));

        Mockito.verify(delegate, Mockito.times(2)).geocode(placeId_1);
        Mockito.verify(delegate, Mockito.times(1)).geocode(placeId_2);
        Mockito.verify(delegate, Mockito.never()).reverseGeocode(Mockito.any(LatLon.class));
        Mockito.verify(delegate, Mockito.never()).lookup(Mockito.anyString());
    }

    @Test
    public void testReverseCaching() throws Exception {
        final Random rnd = new Random();
        final LatLon loc_1 = new LatLon(-90d + 180d * rnd.nextDouble(), -180d + 360d * rnd.nextDouble()),
                loc_2 = new LatLon(-90d + 180d * rnd.nextDouble(), -180d + 360d * rnd.nextDouble());
        final String placeId_1 = UUID.randomUUID().toString(), placeId_2 = UUID.randomUUID().toString();
        final Place place_1 = new Place(placeId_1).setLatLon(loc_1),
                place_2 = new Place(placeId_2).setLatLon(loc_2);

        final Geocoder delegate = Mockito.mock(Geocoder.class);
        Mockito.when(delegate.reverseGeocode(loc_1)).thenReturn(Collections.singletonList(place_1));
        Mockito.when(delegate.reverseGeocode(loc_2)).thenReturn(Collections.singletonList(place_2));

        final CacheGeocoder cache = new CacheGeocoder(delegate, 2, TimeUnit.SECONDS);
        assertThat(cache.reverseGeocode(loc_1), Matchers.contains(place_1));
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_1);
        assertThat(cache.reverseGeocode(loc_1), Matchers.contains(place_1));
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_1);

        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        assertThat(cache.reverseGeocode(loc_1), Matchers.contains(place_1));
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_1);
        assertThat(cache.reverseGeocode(loc_2), Matchers.contains(place_2));
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_2);

        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        assertThat(cache.reverseGeocode(loc_2), Matchers.contains(place_2));
        assertThat(cache.reverseGeocode(loc_1), Matchers.contains(place_1));

        Mockito.verify(delegate, Mockito.never()).geocode(Mockito.anyString());
        Mockito.verify(delegate, Mockito.times(2)).reverseGeocode(loc_1);
        Mockito.verify(delegate, Mockito.times(1)).reverseGeocode(loc_2);
        Mockito.verify(delegate, Mockito.never()).lookup(Mockito.anyString());
    }

}

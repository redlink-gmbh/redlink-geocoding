package io.redlink.geocoding.cache;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by fonso on 31.01.17.
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
        when(mockGeocoder.geocode(anyString())).thenReturn( Arrays.asList(mockPlace));
        when(mockGeocoder.reverseGeocode(any(LatLon.class))).thenReturn(Arrays.asList(mockPlace));
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
}

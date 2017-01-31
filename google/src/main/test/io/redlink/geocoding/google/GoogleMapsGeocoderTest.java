package io.redlink.geocoding.google;

import com.google.maps.GeoApiContext;
import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.Proxy;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.when;

/**
 * Created by fonso on 31.01.17.
 */
public class GoogleMapsGeocoderTest {

    private final String testPlaceId = "ChIJz0qJMpqadkcRpaXIPyX0sI8";
    public  final String testFormattedAddress = "Jakob-Haringer-Straße 3, 5020 Salzburg, Austria";
    private final String testAddress = "jakob haringer strasse 3";
    private final Double testLat = 47.8227343;
    private final Double testLon = 13.0408988;

    private final GeoApiContext context = new GeoApiContext();
    private final GoogleMapsGeocoder gmGeocoder = new GoogleMapsGeocoder(context, Locale.forLanguageTag("en"));

    @Mock
    private LatLon latLon;

    public GoogleMapsGeocoderTest() {
        MockitoAnnotations.initMocks(this);
        context.setApiKey("AIzaSyD3PRRjN1TXyhtE3M8nTf66NNWjGNrtIGA&");
    }

    @Before
    public void init() {
        when(latLon.lat()).thenReturn(testLat);
        when(latLon.lon()).thenReturn(testLon);
    }

    @Test
    public void testGeocode() throws IOException {
        final List<Place> places = gmGeocoder.geocode(testAddress);

        Assert.assertEquals(1, places.size());
        Assert.assertEquals(testPlaceId, places.get(0).getPlaceId());
        Assert.assertEquals(testFormattedAddress, places.get(0).getAddress());
        Assert.assertEquals(testLat, places.get(0).getLatLon().lat(),0);
        Assert.assertEquals(testLon, places.get(0).getLatLon().lon(),0);
    }

    @Test
    public void testReverseGeocode() throws IOException {
        final List<Place> places = gmGeocoder.reverseGeocode(latLon);

        Assert.assertEquals(8, places.size());
        Assert.assertEquals(testPlaceId, places.get(1).getPlaceId());
        Assert.assertEquals(testFormattedAddress, places.get(1).getAddress());
        Assert.assertEquals(testLat, places.get(1).getLatLon().lat(),0);
        Assert.assertEquals(testLon, places.get(1).getLatLon().lon(),0);
    }

    @Test
    public void testLookup() throws IOException {

        final Place place = gmGeocoder.lookup(testPlaceId);

        Assert.assertEquals(testPlaceId, place.getPlaceId());
        Assert.assertEquals(testFormattedAddress, place.getAddress());
        Assert.assertEquals(testLat, place.getLatLon().lat(),0);
        Assert.assertEquals(testLon, place.getLatLon().lon(),0);

    }

}

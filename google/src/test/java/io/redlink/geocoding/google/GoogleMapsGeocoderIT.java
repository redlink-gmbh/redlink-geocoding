package io.redlink.geocoding.google;

import com.google.maps.GeoApiContext;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 */
public class GoogleMapsGeocoderIT {

    private final String testPlaceId = "ChIJz0qJMpqadkcRpaXIPyX0sI8";
    private  final String testFormattedAddress = "Jakob-Haringer-Straße 3, 5020 Salzburg, Austria";
    private final String testAddress = "jakob haringer strasse 3";
    private final Double testLat = 47.8227343;
    private final Double testLon = 13.0408988;

    private final GeoApiContext context = new GeoApiContext();
    private final GoogleMapsGeocoder gmGeocoder = new GoogleMapsGeocoder(context, Locale.forLanguageTag("en"));

    private LatLon latLon = new LatLon(testLat, testLon);

    public GoogleMapsGeocoderIT() {
        final String apiKey = System.getProperty("google.apiKey");
        Assume.assumeThat("Google API-Key missing, provide it with -Dgoogle.apiKey", apiKey, Matchers.not(Matchers.isEmptyOrNullString()));
        Assume.assumeThat("Invalid Google API-Key (expected to start with ''", apiKey, Matchers.startsWith("AIza"));

        context.setApiKey(apiKey);
        context.setChannel(getClass().getSimpleName());
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
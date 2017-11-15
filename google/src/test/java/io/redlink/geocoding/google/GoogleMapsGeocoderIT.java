package io.redlink.geocoding.google;

import com.google.maps.GeoApiContext;
import com.google.maps.internal.ApiConfig;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 */
public class GoogleMapsGeocoderIT {

    static {
        LoggerFactory.getLogger(GoogleMapsGeocoderIT.class)
                .debug("Enable jul-to-slf4j bridge");
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private final String testPlaceId = "ChIJz0qJMpqadkcRpaXIPyX0sI8";
    private final String testFormattedAddress = "Jakob-Haringer-Straße 3, 5020 Salzburg, Austria";
    private final String testAddress = "jakob haringer strasse 3";
    private final Double testLat = 47.82273;
    private final Double testLon = 13.040612;

    private final GeoApiContext context;
    private final GoogleMapsGeocoder gmGeocoder;

    private LatLon latLon = new LatLon(testLat, testLon);

    public GoogleMapsGeocoderIT() {
        final String apiKey = System.getProperty("google.apiKey");
        Assume.assumeThat("Google API-Key missing, provide it with -Dgoogle.apiKey", apiKey, Matchers.not(Matchers.isEmptyOrNullString()));
        Assume.assumeThat("Invalid Google API-Key (expected to start with ''", apiKey, Matchers.startsWith("AIza"));

        context = new GeoApiContext();
        context.setApiKey(apiKey);
        context.setChannel(getClass().getSimpleName());
        gmGeocoder = new GoogleMapsGeocoder(context, Locale.forLanguageTag("en"));
    }

    @Before
    public void pingRemote() {
        try {
            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .head()
                    .url(new ApiConfig("/").hostName)
                    .build();

            final Response response = client.newCall(request).execute();
            Assume.assumeTrue("Remote Service Status", response.isSuccessful());
        } catch (IOException e) {
            Assume.assumeNoException("Ping to remote service failed", e);
        }
    }

    @Test
    public void testGeocode() throws IOException {
        final List<Place> places = gmGeocoder.geocode(testAddress);

        Assert.assertEquals(1, places.size());
        Assert.assertEquals(testPlaceId, places.get(0).getPlaceId());
        Assert.assertEquals(testFormattedAddress, places.get(0).getAddress());
        Assert.assertEquals(testLat, places.get(0).getLatLon().lat(),1e-3);
        Assert.assertEquals(testLon, places.get(0).getLatLon().lon(),1e-3);
    }

    @Test
    public void testReverseGeocode() throws IOException {
        final List<Place> places = gmGeocoder.reverseGeocode(latLon);

        Assert.assertEquals(8, places.size());
        Assert.assertEquals("ChIJ-eEoNJqadkcR3vHpTn3iM_A", places.get(0).getPlaceId());
        Assert.assertEquals("Techno-Z III, 5020 Salzburg, Austria", places.get(0).getAddress());
        Assert.assertEquals(47.8226684, places.get(0).getLatLon().lat(),1e-3);
        Assert.assertEquals(13.0409604, places.get(0).getLatLon().lon(),1e-3);
    }

    @Test
    public void testLookup() throws IOException {
        final Place place = gmGeocoder.lookup(testPlaceId);

        Assert.assertEquals(testPlaceId, place.getPlaceId());
        Assert.assertEquals(testFormattedAddress, place.getAddress());
        Assert.assertEquals(testLat, place.getLatLon().lat(),1e-3);
        Assert.assertEquals(testLon, place.getLatLon().lon(),1e-3);

    }

}

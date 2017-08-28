package io.redlink.geocoding.nominatim;

import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public class NominatimGeocoderIT {

    private final String testPlaceId = "W30514164";
    private final String testFormattedAddress = "Techno-Z III, Jakob-Haringer-Straße, Techno-Z, Itzling, Salzburg, 5020, Austria";
    private final String coworkingFormattedAddress = "Coworkingspace Salzburg, 3, Jakob-Haringer-Straße, Techno-Z, Itzling, Salzburg, 5020, Austria";
    private final String testAddress = "jakob haringer strasse 3";
    private final double testLat = 47.8227343;
    private final double testLon = 13.0408988;

    private final LatLon latLon;

    private final NominatimGeocoder osmGeocoder;

    public NominatimGeocoderIT() {
        latLon = mock(LatLon.class);
        when(latLon.lat()).thenReturn(testLat);
        when(latLon.lon()).thenReturn(testLon);

        osmGeocoder = new NominatimGeocoder(NominatimGeocoder.PUBLIC_NOMINATIM_SERVER,
                Locale.forLanguageTag("en"),
                System.getProperty("nominatim.email"),
                null);
    }

    @Before
    public void pingRemote() {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            final StatusLine statusLine = client.execute(new HttpHead(NominatimGeocoder.PUBLIC_NOMINATIM_SERVER), HttpResponse::getStatusLine);
            Assume.assumeThat("Remote Service Status", statusLine.getStatusCode(), Matchers.allOf(Matchers.greaterThanOrEqualTo(200), Matchers.lessThan(300)));
        } catch (IOException e) {
            Assume.assumeNoException("Ping to remote service failed", e);
        }
    }

    @Test
    public void testGeocode() throws IOException {
        final List<Place> places = osmGeocoder.geocode(testAddress);

        Assert.assertEquals(2, places.size());
        Assert.assertEquals("N3081433444", places.get(0).getPlaceId());
        Assert.assertEquals(coworkingFormattedAddress, places.get(0).getAddress());
        Assert.assertEquals(47.8229144, places.get(0).getLatLon().lat(), 0);
        Assert.assertEquals(13.0404834, places.get(0).getLatLon().lon(), 0);
    }

    @Test
    public void testReverseGeocode() throws IOException {
        final List<Place> places = osmGeocoder.reverseGeocode(latLon);

        Assert.assertEquals(1, places.size());
        Assert.assertEquals(testPlaceId, places.get(0).getPlaceId());
        Assert.assertEquals(testFormattedAddress, places.get(0).getAddress());
        Assert.assertEquals(testLat, places.get(0).getLatLon().lat(), 0);
        Assert.assertEquals(testLon, places.get(0).getLatLon().lon(), 0);
    }

    @Test
    public void testLookup() throws IOException {
        final Place place = osmGeocoder.lookup(testPlaceId);

        Assert.assertEquals(testPlaceId, place.getPlaceId());
        Assert.assertEquals(testFormattedAddress, place.getAddress());
        Assert.assertEquals(testLat, place.getLatLon().lat(), 0.01);
        Assert.assertEquals(testLon, place.getLatLon().lon(), 0.01);

    }
}

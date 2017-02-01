package io.redlink.geocoding.google;

import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by fonso on 30.01.17.
 */
public class GoogleUtilsTest {

    private final GeocodingResult googleMockedGeoResult = new GeocodingResult();
    private final Geometry googleGeometry = new Geometry();
    private final LatLng googleMockedLocation = new LatLng(45.00,12.00);

    private PlaceDetails googlePlaceDetails = new PlaceDetails();

    private final LatLon redlinkLatLon = new LatLon(45.00,12.00);

    @Before
    public void setup() {
        googleGeometry.location = googleMockedLocation;

        googleMockedGeoResult.placeId = "00000000";
        googleMockedGeoResult.formattedAddress = "Test Address 0, 00 - 0000 Test Region";
        googleMockedGeoResult.geometry = googleGeometry;

        googlePlaceDetails.placeId = "00000000";
        googlePlaceDetails.formattedAddress = "Test Address 0, 00 - 0000 Test Region";
        googlePlaceDetails.geometry = googleGeometry;
    }


    @Test
    public void testGoogle2Places() {
        final List<Place> places = GoogleUtils.google2Places(new GeocodingResult[]{googleMockedGeoResult});

        Assert.assertEquals(1,places.size());
        Assert.assertEquals("00000000",places.get(0).getPlaceId());
        Assert.assertEquals("Test Address 0, 00 - 0000 Test Region",places.get(0).getAddress());
        Assert.assertEquals(45.00, places.get(0).getLatLon().lat(),0);
        Assert.assertEquals(12.00,places.get(0).getLatLon().lon(),0);
    }

    @Test
    public void testPlaceDetails2Place() {
        Place place = GoogleUtils.placeDetails2Place(googlePlaceDetails);

        Assert.assertEquals("00000000",place.getPlaceId());
        Assert.assertEquals("Test Address 0, 00 - 0000 Test Region",place.getAddress());
        Assert.assertEquals(45.00, place.getLatLon().lat(),0);
        Assert.assertEquals(12.00,place.getLatLon().lon(),0);

    }

    @Test
    public void testLatLon2LatLng() {
        final LatLng latLng = GoogleUtils.latLon2LatLng(redlinkLatLon);

        Assert.assertEquals(45.00, latLng.lat, 0);
        Assert.assertEquals(12.00,latLng.lng,0);

    }



}

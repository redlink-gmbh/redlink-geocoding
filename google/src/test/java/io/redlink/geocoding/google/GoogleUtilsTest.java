package io.redlink.geocoding.google;

import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by fonso on 30.01.17.
 */
class GoogleUtilsTest {

    private final GeocodingResult googleMockedGeoResult = new GeocodingResult();
    private final Geometry googleGeometry = new Geometry();
    private final LatLng googleMockedLocation = new LatLng(45.00, 12.00);

    private PlaceDetails googlePlaceDetails = new PlaceDetails();

    private final LatLon redlinkLatLon = new LatLon(45.00, 12.00);

    @BeforeEach
    void setup() {
        googleGeometry.location = googleMockedLocation;

        googleMockedGeoResult.placeId = "00000000";
        googleMockedGeoResult.formattedAddress = "Test Address 0, 00 - 0000 Test Region";
        googleMockedGeoResult.geometry = googleGeometry;

        googlePlaceDetails.placeId = "00000000";
        googlePlaceDetails.formattedAddress = "Test Address 0, 00 - 0000 Test Region";
        googlePlaceDetails.geometry = googleGeometry;
    }


    @Test
    void testGoogle2Places() {
        final List<Place> places = GoogleUtils.google2Places(new GeocodingResult[]{googleMockedGeoResult});

        assertThat(places)
                .as("convert Google to Places")
                .singleElement()
                .hasFieldOrPropertyWithValue("placeId", "00000000")
                .hasFieldOrPropertyWithValue("address", "Test Address 0, 00 - 0000 Test Region")
                .extracting(Place::getLatLon)
                .hasFieldOrPropertyWithValue("lat", 45D)
                .hasFieldOrPropertyWithValue("lon", 12D);
    }

    @Test
    void testPlaceDetails2Place() {
        Place place = GoogleUtils.placeDetails2Place(googlePlaceDetails);

        assertThat(place)
                .as("convert Google PlaceDetails to Place")
                .hasFieldOrPropertyWithValue("placeId", "00000000")
                .hasFieldOrPropertyWithValue("address", "Test Address 0, 00 - 0000 Test Region")
                .extracting(Place::getLatLon)
                .hasFieldOrPropertyWithValue("lat", 45D)
                .hasFieldOrPropertyWithValue("lon", 12D);

    }

    @Test
    void testLatLon2LatLng() {
        final LatLng latLng = GoogleUtils.latLon2LatLng(redlinkLatLon);

        assertThat(latLng)
                .as("convert LatLon to Google LatLng")
                .hasFieldOrPropertyWithValue("lat", 45D)
                .hasFieldOrPropertyWithValue("lng", 12D);
    }

}

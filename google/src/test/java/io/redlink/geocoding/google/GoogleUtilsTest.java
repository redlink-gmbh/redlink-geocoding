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
    private static final LatLng GOOGLE_MOCKED_LOCATION = new LatLng(45.00, 12.00);
    private static final LatLon REDLINK_LAT_LON = LatLon.create(45.00, 12.00);

    private final GeocodingResult googleMockedGeoResult = new GeocodingResult();
    private final Geometry googleGeometry = new Geometry();
    private final PlaceDetails googlePlaceDetails = new PlaceDetails();


    @BeforeEach
    void setup() {
        googleGeometry.location = GOOGLE_MOCKED_LOCATION;

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
        final LatLng latLng = GoogleUtils.latLon2LatLng(REDLINK_LAT_LON);

        assertThat(latLng)
                .as("convert LatLon to Google LatLng")
                .hasFieldOrPropertyWithValue("lat", 45D)
                .hasFieldOrPropertyWithValue("lng", 12D);
    }

}

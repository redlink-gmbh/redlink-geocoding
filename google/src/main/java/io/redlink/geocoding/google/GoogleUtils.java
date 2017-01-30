/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.google;

import com.google.common.collect.Lists;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;

import java.util.Arrays;
import java.util.List;

/**
 * Converting Utils Google &lt;---&gt; Redlink.
 */
public class GoogleUtils {

    public static List<Place> google2Places(GeocodingResult[] results) {
        return Lists.transform(Arrays.asList(results),
                GoogleUtils::geocodingResult2Place);
    }

    private static Place geocodingResult2Place(GeocodingResult google) {
        final Place place = new Place(google.placeId);
        place.setAddress(google.formattedAddress);
        place.setLatLon(latLng2latLon(google.geometry.location));
        return place;
    }

    public static LatLng latLon2LatLng(LatLon coordinates) {
        return new LatLng(coordinates.lat(), coordinates.lon());
    }

    public static Place placeDetails2Place(PlaceDetails placeDetails) {
        final Place place = new Place(placeDetails.placeId);
        place.setAddress(placeDetails.formattedAddress);
        place.setLatLon(latLng2latLon(placeDetails.geometry.location));
        return place;
    }

    private static LatLon latLng2latLon(LatLng latLng) {
        return new LatLon(latLng.lat, latLng.lng);
    }
}

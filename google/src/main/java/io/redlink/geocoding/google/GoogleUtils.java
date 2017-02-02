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
        return Place.create(google.placeId,
                google.formattedAddress,
                latLng2latLon(google.geometry.location));
    }

    public static LatLng latLon2LatLng(LatLon coordinates) {
        return new LatLng(coordinates.lat(), coordinates.lon());
    }

    public static Place placeDetails2Place(PlaceDetails placeDetails) {
        return Place.create(placeDetails.placeId,
                placeDetails.formattedAddress,
                latLng2latLon(placeDetails.geometry.location));
    }

    private static LatLon latLng2latLon(LatLng latLng) {
        return new LatLon(latLng.lat, latLng.lng);
    }
}

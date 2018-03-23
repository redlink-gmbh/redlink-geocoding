/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding;

import java.io.IOException;
import java.util.List;

/**
 */
public interface Geocoder {

    default List<Place> geocode(String address) throws IOException {
        return geocode(address, null);
    }
    List<Place> geocode(String address, String language) throws IOException;

    List<Place> reverseGeocode(LatLon coordinates) throws IOException;

    Place lookup(String placeId) throws IOException;

}

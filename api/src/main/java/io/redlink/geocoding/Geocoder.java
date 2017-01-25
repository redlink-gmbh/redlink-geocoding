/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding;

import java.util.List;

/**
 */
public interface Geocoder {

    List<Place> geocode(String address);

    List<Place> reverseGeocode(LatLon coordinates);

    Place lookup(String placeId);

}

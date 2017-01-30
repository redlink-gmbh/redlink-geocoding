/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding;

import java.io.IOException;
import java.util.List;

/**
 */
public interface Geocoder {

    List<Place> geocode(String address) throws IOException;

    List<Place> reverseGeocode(LatLon coordinates) throws IOException;

    Place lookup(String placeId) throws IOException;

}

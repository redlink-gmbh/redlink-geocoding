/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 */
public interface Geocoder {

    default List<Place> geocode(String address) throws IOException {
        return geocode(address, (Locale) null);
}

    default List<Place> geocode(String address, String language) throws IOException {
        return geocode(address, Locale.forLanguageTag(language));
    }

    List<Place> geocode(String address, Locale lang) throws IOException;

    default List<Place> reverseGeocode(LatLon coordinates) throws IOException {
        return reverseGeocode(coordinates, (Locale) null);
    }

    default List<Place> reverseGeocode(LatLon coordinates, String language) throws IOException {
        return reverseGeocode(coordinates, language != null ? Locale.forLanguageTag(language) : null);
    }

    List<Place> reverseGeocode(LatLon coordinates, Locale lang) throws IOException;

    default Optional<Place> lookup(String placeId) throws IOException {
        return lookup(placeId, (Locale) null);
    }

    default Optional<Place> lookup(String placeId, String language) throws IOException {
        return lookup(placeId, Locale.forLanguageTag(language));
    }

    Optional<Place> lookup(String placeId, Locale lang) throws IOException;

}

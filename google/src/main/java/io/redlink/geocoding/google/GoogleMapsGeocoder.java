/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.google;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlacesApi;
import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static io.redlink.geocoding.google.GoogleUtils.google2Places;
import static io.redlink.geocoding.google.GoogleUtils.latLon2LatLng;
import static io.redlink.geocoding.google.GoogleUtils.placeDetails2Place;

/**
 * Implementation of Geocoder backed by GoogleMaps.
 */
public class GoogleMapsGeocoder implements Geocoder {

    private final GeoApiContext context;
    private final Locale language;

    protected GoogleMapsGeocoder(GeoApiContext context, Locale lang) {
        this.context = context;
        language = lang;
    }

    @Override
    public List<Place> geocode(String address) throws IOException {
        try {
            return google2Places(GeocodingApi.geocode(context, address)
                    .language(language.toLanguageTag())
                    .await());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<Place> reverseGeocode(LatLon coordinates) throws IOException {
        try {
            return google2Places(GeocodingApi.reverseGeocode(context, latLon2LatLng(coordinates))
                    .language(language.toLanguageTag())
                    .await());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public Place lookup(String placeId) throws IOException {
        try {
            return placeDetails2Place(PlacesApi.placeDetails(context, placeId)
                    .language(language.toLanguageTag())
                    .await());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static GoogleMapsBuilder configure() {
        return new GoogleMapsBuilder();
    }
}

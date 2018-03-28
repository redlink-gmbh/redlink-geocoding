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

import static io.redlink.geocoding.google.GoogleUtils.*;

/**
 * Implementation of Geocoder backed by GoogleMaps.
 */
public class GoogleMapsGeocoder implements Geocoder {

    private final GeoApiContext context;
    private final Locale language;
    private final boolean apiKeySet, cryptoSecretSet;

    protected GoogleMapsGeocoder(GeoApiContext context, Locale lang, boolean cryptoSecretSet, boolean apiKeySet) {
        this.context = context;
        this.language = lang;
        this.cryptoSecretSet = cryptoSecretSet;
        this.apiKeySet = apiKeySet;
    }

    @Override
    public List<Place> geocode(String address, Locale lang) throws IOException {
        try {
            return google2Places(GeocodingApi.geocode(context, address)
                    .language((lang == null ? language : lang).toLanguageTag())
                    .await());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<Place> reverseGeocode(LatLon coordinates, Locale lang) throws IOException {
        try {
            return google2Places(GeocodingApi.reverseGeocode(context, latLon2LatLng(coordinates))
                    .language((lang == null ? language : lang).toLanguageTag())
                    .await());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public Place lookup(String placeId, Locale lang) throws IOException {
        try {
            return placeDetails2Place(PlacesApi.placeDetails(context, placeId)
                    .language((lang == null ? language : lang).toLanguageTag())
                    .await());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    
    
    @Override
    public String toString() {
        return "GoogleMapsGeocoder [auth:"+ (cryptoSecretSet ? " cryptoSecret" : "") + (apiKeySet ? " apiKey" : "") + ",language=" + language + "]";
    }

    public static GoogleMapsBuilder configure() {
        return new GoogleMapsBuilder();
    }
}

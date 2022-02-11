/*
 * Copyright (c) 2017-2022 Redlink GmbH.
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

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlacesApi;
import com.google.maps.errors.InvalidRequestException;
import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.redlink.geocoding.google.GoogleUtils.google2Places;
import static io.redlink.geocoding.google.GoogleUtils.latLon2LatLng;
import static io.redlink.geocoding.google.GoogleUtils.placeDetails2Place;

/**
 * Implementation of Geocoder backed by GoogleMaps.
 */
public class GoogleMapsGeocoder implements Geocoder {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleMapsGeocoder.class);

    private final GeoApiContext context;
    private final Locale language;
    private final boolean apiKeySet;
    private final boolean cryptoSecretSet;

    protected GoogleMapsGeocoder(GeoApiContext context, Locale lang, boolean cryptoSecretSet, boolean apiKeySet) {
        this.context = context;
        this.language = lang;
        this.cryptoSecretSet = cryptoSecretSet;
        this.apiKeySet = apiKeySet;
    }

    @Override
    public List<Place> geocode(String address, Locale lang) throws IOException {
        try {
            final List<Place> places = google2Places(GeocodingApi.geocode(context, address)
                    .language((lang == null ? language : lang).toLanguageTag())
                    .await());
            LOG.debug("Geocoding '{}' resulted in {} places", address, places.size());
            return places;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<Place> reverseGeocode(LatLon coordinates, Locale lang) throws IOException {
        try {
            final List<Place> places = google2Places(GeocodingApi.reverseGeocode(context, latLon2LatLng(coordinates))
                    .language((lang == null ? language : lang).toLanguageTag())
                    .await());
            LOG.debug("Reverse-Geocoding '{}' resulted in {} places", coordinates, places.size());
            return places;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public Optional<Place> lookup(String placeId, Locale lang) throws IOException {
        try {
            final Place place = placeDetails2Place(PlacesApi.placeDetails(context, placeId)
                    .language((lang == null ? language : lang).toLanguageTag())
                    .await());
            LOG.debug("Lookup of {} resulted in {}", placeId, place);
            return Optional.of(place);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        } catch (InvalidRequestException e) {
            LOG.trace("Invalid Request for lookup {}", placeId, e);
            return Optional.empty();
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

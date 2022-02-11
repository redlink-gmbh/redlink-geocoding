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

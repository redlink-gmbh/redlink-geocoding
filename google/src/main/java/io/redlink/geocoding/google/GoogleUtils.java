/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.google;

import com.google.common.collect.Lists;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import io.redlink.geocoding.AddressComponent.Type;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converting Utils Google &lt;---&gt; Redlink.
 */
final class GoogleUtils {

    private GoogleUtils() {
    }

    public static List<Place> google2Places(GeocodingResult[] results) {
        return Lists.transform(Arrays.asList(results),
                GoogleUtils::geocodingResult2Place);
    }

    private static Place geocodingResult2Place(GeocodingResult google) {
        return Place.create(google.placeId,
                google.formattedAddress,
                latLng2latLon(google.geometry.location),
                mapAddressComponents(google.addressComponents),
                Map.of());
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
        return LatLon.create(latLng.lat, latLng.lng);
    }

    private static Collection<io.redlink.geocoding.AddressComponent> mapAddressComponents(AddressComponent[] acs) {
        if (acs == null) {
            return Collections.emptyList();
        }
        final Map<Type, io.redlink.geocoding.AddressComponent> components = new EnumMap<>(Type.class);
        Arrays.stream(acs).forEach(ac -> {
            EnumSet<AddressComponentType> types = EnumSet.noneOf(AddressComponentType.class);
            Arrays.stream(ac.types).collect(Collectors.toCollection(() -> types));
            if (types.contains(AddressComponentType.COUNTRY)) {
                components.putIfAbsent(Type.country, io.redlink.geocoding.AddressComponent.create(Type.country, ac.longName));
                components.putIfAbsent(Type.countryCode, io.redlink.geocoding.AddressComponent.create(Type.countryCode, ac.shortName.toLowerCase(Locale.ROOT)));
            }
            if (types.contains(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1)) {
                components.putIfAbsent(Type.state, io.redlink.geocoding.AddressComponent.create(Type.state, ac.longName));
            }
            if (types.contains(AddressComponentType.POSTAL_CODE)) {
                components.putIfAbsent(Type.postalCode, io.redlink.geocoding.AddressComponent.create(Type.postalCode, ac.longName));
            }
            if (types.contains(AddressComponentType.LOCALITY)) {
                components.putIfAbsent(Type.city, io.redlink.geocoding.AddressComponent.create(Type.city, ac.longName));
            }
            if (types.contains(AddressComponentType.SUBLOCALITY_LEVEL_1)) {
                components.putIfAbsent(Type.sublocality, io.redlink.geocoding.AddressComponent.create(Type.sublocality, ac.longName));
            }
            if (types.contains(AddressComponentType.ROUTE)) {
                components.putIfAbsent(Type.street, io.redlink.geocoding.AddressComponent.create(Type.street, ac.longName));
            }
            if (types.contains(AddressComponentType.STREET_NUMBER)) {
                components.putIfAbsent(Type.streetNumber, io.redlink.geocoding.AddressComponent.create(Type.streetNumber, ac.longName));
            }
        });
        return components.values();
    }

}

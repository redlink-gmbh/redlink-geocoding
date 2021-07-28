/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A Place on Earth - in most cases.
 */
public final class Place {

    private final String placeId;
    private final String address;
    private final LatLon latLon;

    private final Collection<AddressComponent> components;
    private final Map<String, String> metadata;

    private Place(String placeId, String address, LatLon latLon, Collection<AddressComponent> components, Map<String, String> metadata) {
        this.placeId = placeId;
        this.address = address;
        this.latLon = latLon;
        this.components = components;
        this.metadata = metadata;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getAddress() {
        return address;
    }

    public LatLon getLatLon() {
        return latLon;
    }

    public Collection<AddressComponent> getComponents() {
        return Set.copyOf(components);
    }

    public Map<String, String> getMetadata() {
        return Map.copyOf(metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Place)) return false;
        Place place = (Place) o;
        return Objects.equals(placeId, place.placeId) &&
                Objects.equals(address, place.address) &&
                Objects.equals(latLon, place.latLon) &&
                Objects.equals(components, place.components) &&
                Objects.equals(metadata, place.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, address, latLon, components, metadata);
    }

    @Override
    public String toString() {
        return "Place{" +
                "placeId='" + placeId + '\'' +
                ", address='" + address + '\'' +
                ", latLon=" + latLon +
                '}';
    }

    public static Place create(String placeId, String address, LatLon latLon) {
        return create(placeId, address, latLon, Set.of(), Map.of());
    }

    public static Place create(String placeId, String address, LatLon latLon,
                               Collection<AddressComponent> addressComponents, Map<String, String> metadata) {
        return new Place(placeId, address, latLon, addressComponents, metadata);
    }
}

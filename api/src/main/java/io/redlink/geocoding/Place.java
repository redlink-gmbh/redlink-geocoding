/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * A Place on Earth - in most cases.
 */
public class Place {
    private String placeId;
    private String address;
    private final Collection<AddressComponent> components = new LinkedList<>();
    private final Map<String,Object> metadata = new HashMap<>();
    
    private LatLon latLon;

    protected Place() {
    }

    protected Place(String placeId) {
        this();
        this.placeId = placeId;
    }

    public String getPlaceId() {
        return placeId;
    }

    protected Place setPlaceId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    protected Place setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getAddress() {
        return address;
    }

    protected Place setLatLon(LatLon latLon) {
        this.latLon = latLon;
        return this;
    }

    public LatLon getLatLon() {
        return latLon;
    }
    
    public Collection<AddressComponent> getComponents() {
        return components;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Place)) return false;
        Place place = (Place) o;
        return Objects.equals(placeId, place.placeId) &&
                Objects.equals(address, place.address) &&
                Objects.equals(latLon, place.latLon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, address, latLon);
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
        return new Place(placeId).setAddress(address).setLatLon(latLon);
    }
}

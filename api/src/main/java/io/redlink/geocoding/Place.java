/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding;

/**
 * A Place on Earth - in most cases.
 */
public class Place {
    private String placeId;
    private String address;
    private LatLon latLon;

    public Place() {
    }

    public Place(String placeId) {
        this();
        this.placeId = placeId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public Place setPlaceId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    public Place setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Place setLatLon(LatLon latLon) {
        this.latLon = latLon;
        return this;
    }

    public LatLon getLatLon() {
        return latLon;
    }
}

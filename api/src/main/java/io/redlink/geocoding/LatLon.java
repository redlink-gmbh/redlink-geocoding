/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding;

/**
 * A position on earth, identified by latitude and longitude.
 */
public class LatLon {
    private double lat, lon;

    public LatLon(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double lat() {
        return lat;
    }

    public double lon() {
        return lon;
    }
}

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

    public static LatLon valueOf(String lat, String lon) {
        return new LatLon(Double.parseDouble(lat), Double.parseDouble(lon));
    }

    public static LatLon valueOf(String latLon) {
        final String[] s = latLon.split(",", 2);
        return valueOf(s[0], s[1]);
    }

    public boolean equals(Object obj) {
        if (LatLon.class.isAssignableFrom(obj.getClass())) {
            LatLon latLonObj = (LatLon) obj;
            return (this.lat == latLonObj.lat() && this.lon == latLonObj.lon());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return (Double.toString(lat) + Double.toString(lon)).hashCode();
    }

    @Override
    public String toString() {
        return "LatLon{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}

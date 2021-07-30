/*
 * Copyright (c) 2021 Redlink GmbH.
 */
package io.redlink.geocoding.proxy.io;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.redlink.geocoding.LatLon;

public final class LatLonDTO {

    private final double lat;
    private final double lon;

    private LatLonDTO(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @JsonProperty("lat")
    public double getLat() {
        return lat;
    }

    @JsonProperty("lon")
    public double getLon() {
        return lon;
    }

    @JsonIgnore
    public LatLon toLatLon() {
        return LatLon.create(lat, lon);
    }

    public static LatLonDTO fromLatLon(LatLon latLon) {
        return new LatLonDTO(
                latLon.lat(), latLon.lon()
        );
    }

    @JsonCreator
    public static LatLonDTO create(
            @JsonProperty("lat") double lat,
            @JsonProperty("lon") double lon
    ) {
        return new LatLonDTO(lat, lon);
    }
}

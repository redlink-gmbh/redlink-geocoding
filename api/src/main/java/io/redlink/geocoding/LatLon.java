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

import static java.lang.String.format;

/**
 * A position on earth, identified by latitude and longitude.
 */
public final class LatLon {

    private final double lat;
    private final double lon;

    private LatLon(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double lat() {
        return lat;
    }

    public double lon() {
        return lon;
    }

    public static LatLon create(double lat, double lon) {
        return new LatLon(lat, lon);
    }

    public static LatLon valueOf(String lat, String lon) {
        try {
            return create(Double.parseDouble(lat), Double.parseDouble(lon));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(format("Parsing '%s,%s' failed", lat, lon), e);
        }
    }

    public static LatLon valueOf(String latLon) {
        final String[] s = latLon.split(",", 2);
        return valueOf(s[0], s[1]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LatLon latLon = (LatLon) o;
        return Double.compare(latLon.lat, lat) == 0 && Double.compare(latLon.lon, lon) == 0;
    }

    public int hashCode() {
        return (Double.toString(lat) + lon).hashCode();
    }

    @Override
    public String toString() {
        return "LatLon{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}

/*
 * Copyright (c) 2021-2022 Redlink GmbH.
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

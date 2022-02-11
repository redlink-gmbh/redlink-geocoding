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
import io.redlink.geocoding.Place;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class PlaceDTO {

    private final String placeId;
    private final String address;
    private final LatLonDTO latLon;

    private final Collection<AddressComponentDTO> components;
    private final Map<String, String> metadata;

    private PlaceDTO(String placeId, String address, LatLonDTO latLon,
                     Collection<AddressComponentDTO> components, Map<String, String> metadata) {
        this.placeId = placeId;
        this.address = address;
        this.latLon = latLon;
        this.components = components;
        this.metadata = metadata;
    }

    @JsonProperty("placeId")
    public String getPlaceId() {
        return placeId;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("latLon")
    public LatLonDTO getLatLon() {
        return latLon;
    }

    @JsonProperty("components")
    public Collection<AddressComponentDTO> getComponents() {
        return Set.copyOf(components);
    }

    @JsonProperty("metadata")
    public Map<String, String> getMetadata() {
        return Map.copyOf(metadata);
    }

    @JsonIgnore
    public Place toPlace() {
        return Place.create(placeId, address, latLon.toLatLon(),
                components.stream()
                        .map(AddressComponentDTO::toAddressComponent)
                        .collect(Collectors.toSet()),
                metadata);
    }

    public static PlaceDTO fromPlace(Place place) {
        return create(
                place.getPlaceId(),
                place.getAddress(),
                LatLonDTO.fromLatLon(place.getLatLon()),
                place.getComponents().stream()
                        .map(AddressComponentDTO::fromAddressComponent)
                        .collect(Collectors.toSet()),
                place.getMetadata());
    }

    @JsonCreator
    public static PlaceDTO create(
            @JsonProperty("placeId") String placeId,
            @JsonProperty("address") String address,
            @JsonProperty("latLon") LatLonDTO latLon,
            @JsonProperty("components") Collection<AddressComponentDTO> components,
            @JsonProperty("metadata") Map<String, String> metadata
    ) {
        return new PlaceDTO(placeId, address, latLon, components, metadata);
    }

}

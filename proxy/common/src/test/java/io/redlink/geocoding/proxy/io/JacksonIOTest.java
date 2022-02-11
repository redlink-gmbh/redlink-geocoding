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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.redlink.geocoding.AddressComponent;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JacksonIOTest {

    private final ObjectMapper mapper;

    JacksonIOTest() {
        mapper = new ObjectMapper();
    }

    @Test
    void testLanLon() throws JsonProcessingException {
        final LatLon latLon = LatLon.create(randomDouble(-90, 90), randomDouble(-180, 180));

        final String json = mapper.writeValueAsString(LatLonDTO.fromLatLon(latLon));
        final LatLon read = mapper.readValue(json, LatLonDTO.class).toLatLon();

        assertEquals(latLon, read, "IO Roundtrip");
    }

    @Test
    void testPlace() throws JsonProcessingException {
        final String placeId = randomAlphabetic(5, 15);
        final String address = randomAlphabetic(52);
        final LatLon latLon = LatLon.create(randomDouble(-90, 90), randomDouble(-180, 180));

        final Set<AddressComponent> components = Set.of(
                AddressComponent.create(AddressComponent.Type.city, randomAlphabetic(9)),
                AddressComponent.create(AddressComponent.Type.country, randomAlphabetic(9)),
                AddressComponent.create(AddressComponent.Type.street, randomAlphabetic(9)),
                AddressComponent.create(AddressComponent.Type.postalCode, randomAlphabetic(9))
        );
        final Map<String, String> metadata = Map.of(
                "meta-1", randomAlphabetic(1),
                "meta-2", randomAlphabetic(2),
                "meta-3", randomAlphabetic(4),
                "meta-4", randomAlphabetic(8)
        );

        final Place place = Place.create(placeId, address, latLon, components, metadata);
        final String json = mapper.writeValueAsString(PlaceDTO.fromPlace(place));
        final Place read = mapper.readValue(json, PlaceDTO.class).toPlace();

        Assertions.assertThat(read)
                .as("Deserialized Equals")
                .isEqualTo(place)
                .as("Field-Values")
                .hasFieldOrPropertyWithValue("placeId", placeId)
                .hasFieldOrPropertyWithValue("address", address)
                .as("Address-Components")
                .hasFieldOrPropertyWithValue("components", components)
                .as("Metadata")
                .hasFieldOrPropertyWithValue("metadata", metadata);

    }

    @ParameterizedTest
    @EnumSource(AddressComponent.Type.class)
    void testAddressComponent(AddressComponent.Type type) throws JsonProcessingException {

        final AddressComponent ac = AddressComponent.create(type, RandomStringUtils.randomAlphabetic(4, 25));

        final String json = mapper.writeValueAsString(AddressComponentDTO.fromAddressComponent(ac));
        final AddressComponent read = mapper.readValue(json, AddressComponentDTO.class).toAddressComponent();

        assertEquals(ac, read, "IO Roundtrip");
    }

    static double randomDouble(double start, double end) {
        return start + RandomUtils.nextDouble(0, end - start);
    }
}

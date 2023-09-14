/*
 * Copyright (c) 2022 Redlink GmbH.
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

package io.redlink.geocoding.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.redlink.geocoding.AddressComponent;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import io.redlink.geocoding.proxy.io.Endpoints;
import io.redlink.geocoding.proxy.io.PlaceDTO;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static java.lang.String.format;

class ProxyGeocoderTest {

    private static final WireMockServer wiremock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    private static final ObjectMapper mapper = JsonMapper.builder().build();

    private static final Place place1 = createPlace("Hauptstrasse 15");
    private static final Place place2 = createPlace("Am Dorfplatz 1");

    private static final String basePath = RandomStringUtils.randomAlphabetic(6);

    private final ProxyGeocoder geocoder;

    public ProxyGeocoderTest() {
        geocoder = new ProxyGeocoder(URI.create(wiremock.url(basePath)));
    }

    @BeforeAll
    static void beforeAll() throws JsonProcessingException {
        wiremock.start();
        wiremock.stubFor(WireMock.get(WireMock.urlPathEqualTo(format("/%s/%s", basePath, Endpoints.API_VERSION)))
                .willReturn(WireMock.noContent())
        );
        wiremock.stubFor(WireMock.get(WireMock.urlPathEqualTo(format("/%s/%s/%s", basePath, Endpoints.API_VERSION, Endpoints.GEOCODE)))
                .willReturn(WireMock.okJson(mapper.writeValueAsString(
                        List.of(
                                PlaceDTO.fromPlace(place1),
                                PlaceDTO.fromPlace(place2)
                        )
                )))
        );
        wiremock.stubFor(WireMock.get(WireMock.urlPathEqualTo(format("/%s/%s/%s", basePath, Endpoints.API_VERSION, Endpoints.REVERSE_GEOCODE)))
                .willReturn(WireMock.okJson(mapper.writeValueAsString(
                        List.of(
                                PlaceDTO.fromPlace(place2),
                                PlaceDTO.fromPlace(place1)
                        )
                )))
        );
        wiremock.stubFor(WireMock.get(WireMock.urlPathEqualTo(format("/%s/%s/%s", basePath, Endpoints.API_VERSION, Endpoints.LOOKUP)))
                .willReturn(WireMock.okJson(mapper.writeValueAsString(
                        PlaceDTO.fromPlace(place1)
                )))
        );
        wiremock.stubFor(WireMock.get(WireMock.urlPathEqualTo(format("/%s/%s/%s", basePath, Endpoints.API_VERSION, Endpoints.LOOKUP)))
                .withQueryParam(Endpoints.PARAM_PLACE_ID, WireMock.equalTo("does-not-exist"))
                .willReturn(WireMock.notFound())
        );
    }

    @AfterAll
    static void afterAll() {
        wiremock.stop();
    }

    @Test
    void testGeocode() throws IOException {
        Assertions.assertThat(geocoder.geocode("some address"))
                .as("Results")
                .hasSize(2)
                .first()
                .as("First Result Matches")
                .isEqualTo(place1);

    }

    @Test
    void testReverseGeocode() throws IOException {
        Assertions.assertThat(geocoder.reverseGeocode(LatLon.create(15, 35)))
                .as("Results")
                .hasSize(2)
                .first()
                .as("First Result Matches")
                .isEqualTo(place2)
        ;
    }

    @Test
    void testLookup() throws IOException {
        Assertions.assertThat(geocoder.lookup(RandomStringUtils.randomAlphabetic(8)))
                .as("Lookup Result")
                .isPresent()
                .contains(place1);

        Assertions.assertThat(geocoder.lookup("does-not-exist"))
                .as("Lookup Result")
                .isEmpty();
    }

    static Place createPlace(String address) {
        return Place.create(
                RandomStringUtils.randomAlphabetic(8),
                address,
                LatLon.create(RandomUtils.nextDouble(0, 90), RandomUtils.nextDouble(0, 180)),
                Set.of(
                        AddressComponent.create(AddressComponent.Type.street, address)
                ),
                Map.of("source", "testdata")
        );
    }
}
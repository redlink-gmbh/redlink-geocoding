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

    private static final WireMockServer WIREMOCK = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    private static final ObjectMapper MAPPER = JsonMapper.builder().build();

    private static final Place PLACE_1 = createPlace("Hauptstrasse 15");
    private static final Place PLACE_2 = createPlace("Am Dorfplatz 1");

    private static final String BASE_PATH = RandomStringUtils.insecure().nextAlphabetic(6);

    private final ProxyGeocoder geocoder;

    public ProxyGeocoderTest() {
        geocoder = new ProxyGeocoder(URI.create(WIREMOCK.url(BASE_PATH)));
    }

    @BeforeAll
    static void beforeAll() throws JsonProcessingException {
        WIREMOCK.start();
        WIREMOCK.stubFor(WireMock.get(WireMock.urlPathEqualTo(format("/%s/%s", BASE_PATH, Endpoints.API_VERSION)))
                .willReturn(WireMock.noContent())
        );
        WIREMOCK.stubFor(WireMock.get(WireMock.urlPathEqualTo(format("/%s/%s/%s", BASE_PATH, Endpoints.API_VERSION, Endpoints.GEOCODE)))
                .willReturn(WireMock.okJson(MAPPER.writeValueAsString(
                        List.of(
                                PlaceDTO.fromPlace(PLACE_1),
                                PlaceDTO.fromPlace(PLACE_2)
                        )
                )))
        );
        WIREMOCK.stubFor(WireMock.get(WireMock.urlPathEqualTo(format("/%s/%s/%s", BASE_PATH, Endpoints.API_VERSION, Endpoints.REVERSE_GEOCODE)))
                .willReturn(WireMock.okJson(MAPPER.writeValueAsString(
                        List.of(
                                PlaceDTO.fromPlace(PLACE_2),
                                PlaceDTO.fromPlace(PLACE_1)
                        )
                )))
        );
        WIREMOCK.stubFor(WireMock.get(WireMock.urlPathEqualTo(format("/%s/%s/%s", BASE_PATH, Endpoints.API_VERSION, Endpoints.LOOKUP)))
                .willReturn(WireMock.okJson(MAPPER.writeValueAsString(
                        PlaceDTO.fromPlace(PLACE_1)
                )))
        );
        WIREMOCK.stubFor(WireMock.get(WireMock.urlPathEqualTo(format("/%s/%s/%s", BASE_PATH, Endpoints.API_VERSION, Endpoints.LOOKUP)))
                .withQueryParam(Endpoints.PARAM_PLACE_ID, WireMock.equalTo("does-not-exist"))
                .willReturn(WireMock.notFound())
        );
    }

    @AfterAll
    static void afterAll() {
        WIREMOCK.stop();
    }

    @Test
    void testGeocode() throws IOException {
        Assertions.assertThat(geocoder.geocode("some address"))
                .as("Results")
                .hasSize(2)
                .first()
                .as("First Result Matches")
                .isEqualTo(PLACE_1);

    }

    @Test
    void testReverseGeocode() throws IOException {
        Assertions.assertThat(geocoder.reverseGeocode(LatLon.create(15, 35)))
                .as("Results")
                .hasSize(2)
                .first()
                .as("First Result Matches")
                .isEqualTo(PLACE_2)
        ;
    }

    @Test
    void testLookup() throws IOException {
        Assertions.assertThat(geocoder.lookup(RandomStringUtils.insecure().nextAlphabetic(8)))
                .as("Lookup Result")
                .isPresent()
                .contains(PLACE_1);

        Assertions.assertThat(geocoder.lookup("does-not-exist"))
                .as("Lookup Result")
                .isEmpty();
    }

    static Place createPlace(String address) {
        return Place.create(
                RandomStringUtils.insecure().nextAlphabetic(8),
                address,
                LatLon.create(
                        RandomUtils.insecure().randomDouble(0, 90),
                        RandomUtils.insecure().randomDouble(0, 180)
                ),
                Set.of(
                        AddressComponent.create(AddressComponent.Type.street, address)
                ),
                Map.of("source", "testdata")
        );
    }
}
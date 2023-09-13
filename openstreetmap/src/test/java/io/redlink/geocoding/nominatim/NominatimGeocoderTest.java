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

package io.redlink.geocoding.nominatim;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.redlink.geocoding.AddressComponent;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
class NominatimGeocoderTest {

    public static WireMockServer wiremock = new WireMockServer(WireMockConfiguration.options()
            .dynamicPort()
    );

    private final NominatimGeocoder geocoder;

    public NominatimGeocoderTest() {
        geocoder = NominatimGeocoder.builder().setBaseUrl(wiremock.baseUrl()).setLocale(Locale.ENGLISH).create();
    }

    @BeforeAll
    static void beforeClass() throws Exception {
        wiremock.start();
        wiremock.stubFor(urlEndsWith(NominatimGeocoder.SERVICE_GEOCODE)
                .willReturn(createXmlResponse("/geocode-response.xml"))
        );
        wiremock.stubFor(urlEndsWith(NominatimGeocoder.SERVICE_REVERSE)
                .willReturn(createXmlResponse("/reverse-response.xml"))
        );
        wiremock.stubFor(urlEndsWith(NominatimGeocoder.SERVICE_LOOKUP)
                .willReturn(createXmlResponse("/lookup-response.xml"))
        );
    }

    @AfterAll
    static void afterAll() {
        wiremock.stop();
    }

    private static MappingBuilder urlEndsWith(String path) {
        return WireMock.any(
                WireMock.urlPathMatching(
                        String.format(".*\\Q%s\\E$", path)
                )
        );
    }

    private static ResponseDefinitionBuilder createXmlResponse(String responseFile) throws IOException {
        return WireMock.ok()
                .withHeader("Content-Type", "text/xml")
                .withBody(
                        IOUtils.resourceToByteArray(responseFile)
                );
    }

    @Test
    void testGeocode() throws Exception {
        final List<Place> places = geocoder.geocode("135 pilkington, avenue birmingham");

        assertThat(places)
                .as("geocoding results")
                .singleElement()
                .as("geocoded place")
                .hasFieldOrPropertyWithValue("placeId", "W90394480")
                .hasFieldOrPropertyWithValue("address", "135, Pilkington Avenue, Sutton Coldfield, Maney, Birmingham, West Midlands, England, B72 1LH, UK")
                .hasFieldOrPropertyWithValue("latLon", LatLon.valueOf("52.5487921,-1.8164307339635"));
    }

    @Test
    void testReverseGeocode() throws Exception {
        final List<Place> places = geocoder.reverseGeocode(LatLon.valueOf("52.5487429714954,-1.81602098644987"));

        assertThat(places)
                .as("reverse geocoding results")
                .singleElement()
                .as("reverse geocoded place")
                .hasFieldOrPropertyWithValue("placeId", "W90394420")
                .hasFieldOrPropertyWithValue("address", "137, Pilkington Avenue, Sutton Coldfield, Maney, Birmingham, West Midlands, England, B72 1LH, UK")
                .hasFieldOrPropertyWithValue("latLon", LatLon.valueOf("52.5487429714954,-1.81602098644987"));
    }

    @Test
    void testLookup() throws Exception {
        final Optional<Place> place = geocoder.lookup("N240109189");

        assertThat(place)
                .as("place lookup")
                .isPresent().get()
                .hasFieldOrPropertyWithValue("placeId", "N240109189")
                .hasFieldOrPropertyWithValue("address", "Berlin, Deutschland")
                .hasFieldOrPropertyWithValue("latLon", LatLon.valueOf("52.5170365,13.3888599"));
    }

    @Test
    void testReadPlace() {
        // Something that works
        final Place place = Place.create("X123123", "The Street", LatLon.create(15, 25),
                Set.of(AddressComponent.create(AddressComponent.Type.street, "Street")),
                Map.of("source", "test"));
        final Optional<Place> actual = geocoder.readPlace(createPlaceElement(
                place.getPlaceId(), place.getAddress(),
                String.format("%f,%f", place.getLatLon().lat(), place.getLatLon().lon()),
                Map.of(
                        "road", "Street",
                        "source", "test"
                )
        ));
        assertThat(actual)
                .as("Working example")
                .isPresent().get()
                .isEqualTo(place);

        assertThat(geocoder.readPlace(createPlaceElement(
                null, "10 Downing Street", "15,25", Map.of()
        )))
                .as("No PlaceId")
                .isEmpty();
        assertThat(geocoder.readPlace(createPlaceElement(
                "X123", null, "15,25", Map.of()
        )))
                .as("No Street")
                .isEmpty();
        assertThat(geocoder.readPlace(createPlaceElement(
                "X123", "Downtown", "somewhere", Map.of()
        )))
                .as("Invalid Coordinates")
                .isEmpty();
    }

    static Element createPlaceElement(String placeId, String address, String latLon, Map<String, String> components) {
        final Element place = new Element("place")
                .attr("osm_type", StringUtils.repeat(StringUtils.left(placeId, 1), 5))
                .attr("osm_id", StringUtils.substring(placeId, 1))
                .attr("display_name", address)
                .attr("lat", StringUtils.substringBefore(latLon, ","))
                .attr("lon", StringUtils.substringAfter(latLon, ","))
        ;

        components.forEach((k,v) -> new Element(k).text(v).appendTo(place));

        return place;
    }
}
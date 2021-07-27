package io.redlink.geocoding.nominatim;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
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
        geocoder = new NominatimGeocoder(wiremock.baseUrl(), Locale.ENGLISH, null, null);
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




    private static Document loadTestData(String serviceLookup) throws IOException {
        return Jsoup.parse(NominatimGeocoderTest.class.getResourceAsStream(serviceLookup + "-response.xml"),
                "utf-8",
                "http://example.com/",
                Parser.xmlParser());
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
    public void testLookup() throws Exception {
        final Place place = geocoder.lookup("N240109189");

        assertThat(place)
                .as("place lookup")
                .hasFieldOrPropertyWithValue("placeId", "N240109189")
                .hasFieldOrPropertyWithValue("address", "Berlin, Deutschland")
                .hasFieldOrPropertyWithValue("latLon", LatLon.valueOf("52.5170365,13.3888599"));
    }
}
package io.redlink.geocoding.nominatim;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class NominatimGeocoderTest {

    @ClassRule
    public static WireMockRule wiremock = new WireMockRule(WireMockConfiguration.options()
            .dynamicPort()
    );

    private final NominatimGeocoder geocoder;

    public NominatimGeocoderTest() throws IOException, URISyntaxException {
        geocoder = new NominatimGeocoder(wiremock.baseUrl(), Locale.ENGLISH, null, null);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
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
    public void testGeocode() throws Exception {
        final List<Place> places = geocoder.geocode("135 pilkington, avenue birmingham");
        assertThat(places, Matchers.iterableWithSize(1));

        final Place place = places.get(0);
        assertEquals("W90394480", place.getPlaceId());
        assertEquals("135, Pilkington Avenue, Sutton Coldfield, Maney, Birmingham, West Midlands, England, B72 1LH, UK", place.getAddress());
        assertEquals(LatLon.valueOf("52.5487921,-1.8164307339635"), place.getLatLon());
    }

    @Test
    public void testReverseGeocode() throws Exception {
        final List<Place> places = geocoder.reverseGeocode(LatLon.valueOf("52.5487429714954,-1.81602098644987"));
        assertThat(places, Matchers.iterableWithSize(1));

        final Place place = places.get(0);
        assertEquals("W90394420", place.getPlaceId());
        assertEquals("137, Pilkington Avenue, Sutton Coldfield, Maney, Birmingham, West Midlands, England, B72 1LH, UK", place.getAddress());
        assertEquals(LatLon.valueOf("52.5487429714954,-1.81602098644987"), place.getLatLon());

    }

    @Test
    public void testLookup() throws Exception {
        final Place place = geocoder.lookup("N240109189");

        assertEquals("N240109189", place.getPlaceId());
        assertEquals("Berlin, Deutschland", place.getAddress());
        assertEquals(LatLon.valueOf("52.5170365,13.3888599"), place.getLatLon());
    }
}
package io.redlink.geocoding.nominatim;

import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hamcrest.Matchers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 */
public class NominatimGeocoderTest {


    private final NominatimGeocoder geocoder;

    public NominatimGeocoderTest() throws IOException, URISyntaxException {

        final CloseableHttpClient httpClient = Mockito.mock(CloseableHttpClient.class);
        Mockito.when(httpClient.execute(Mockito.any(HttpGet.class), Mockito.any(NominatimGeocoder.JsoupResponseHandler.class)))
                .then(invocation -> {
                    final HttpGet request = (HttpGet) invocation.getArguments()[0];
                    final String requestUri = request.getURI().toASCIIString();
                    final NominatimGeocoder.JsoupResponseHandler handler = (NominatimGeocoder.JsoupResponseHandler) invocation.getArguments()[1];

                    final Document jsoup;
                    if (StringUtils.contains(requestUri, NominatimGeocoder.SERVICE_GEOCODE)) {
                        jsoup = loadTestData("/geocode");
                    } else if (StringUtils.contains(requestUri, NominatimGeocoder.SERVICE_REVERSE)) {
                        jsoup = loadTestData("/reverse");
                    } else if (StringUtils.contains(requestUri, NominatimGeocoder.SERVICE_LOOKUP)) {
                        jsoup = loadTestData("/lookup");
                    } else {
                        throw new ClientProtocolException();
                    }
                    return handler.parseJsoup(jsoup);
                });

        geocoder = Mockito.spy(new NominatimGeocoder("http://example.com/", Locale.ENGLISH, null, null));
        Mockito.when(geocoder.createHttpClient()).thenReturn(httpClient);
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
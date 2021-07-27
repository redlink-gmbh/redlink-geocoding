package io.redlink.geocoding.google;

import com.google.maps.GeoApiContext;
import com.google.maps.internal.ApiConfig;
import io.redlink.geocoding.AddressComponent;
import io.redlink.geocoding.AddressComponent.Type;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.Assumptions.assumeThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 *
 */
class GoogleMapsGeocoderIT {

    static {
        LoggerFactory.getLogger(GoogleMapsGeocoderIT.class)
                .debug("Enable jul-to-slf4j bridge");
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private final String testPlaceId = "ChIJz0qJMpqadkcRpaXIPyX0sI8";
    private final String testFormattedAddress = "Jakob-Haringer-Straße 3, 5020 Salzburg, Austria";
    private final String testAddress = "jakob haringer strasse 3";
    private final Double testLat = 47.82273;
    private final Double testLon = 13.040612;

    private final GoogleMapsGeocoder gmGeocoder;

    private final LatLon latLon = new LatLon(testLat, testLon);

    public GoogleMapsGeocoderIT() {
        final String apiKey = System.getProperty("google.apiKey", System.getenv("GOOGLE_API_KEY"));

        assumeThat(apiKey)
                .as("Google API-Key missing, provide it with -Dgoogle.apiKey")
                .isNotEmpty()
                .as("Invalid Google API-Key (expected to start with 'AIza'")
                .startsWith("AIza");

        GeoApiContext.Builder contextBuilder = new GeoApiContext.Builder();
        contextBuilder.apiKey(apiKey);
        contextBuilder.channel(getClass().getSimpleName());
        gmGeocoder = new GoogleMapsGeocoder(
                contextBuilder.build(),
                Locale.forLanguageTag("en"),
                false,
                true
        );
    }

    @BeforeEach
    void pingRemote() {
        assumeThatCode(() -> {
            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .head()
                    .url(new ApiConfig("/").hostName)
                    .build();

            final Response response = client.newCall(request).execute();
            assumeTrue(response.isSuccessful(), "Remote Service Status");
        })
        .as("Ping to remote service")
        .doesNotThrowAnyException();
    }

    @Test
    void testGeocode() throws IOException {
        final List<Place> places = gmGeocoder.geocode(testAddress);

        assertThat(places)
                .as("Geocoded Place")
                .singleElement()
                .hasFieldOrPropertyWithValue("placeId", testPlaceId)
                .hasFieldOrPropertyWithValue("address", testFormattedAddress);

        Collection<AddressComponent> addrComps = places.get(0).getComponents();

        EnumMap<Type, String> expected = new EnumMap<>(Type.class);
        expected.put(Type.streetNumber, "3");
        expected.put(Type.street, "Jakob-Haringer-Straße");
        expected.put(Type.sublocality, "Itzling");
        expected.put(Type.city, "Salzburg");
        expected.put(Type.postalCode, "5020");
        expected.put(Type.state, "Salzburg");
        expected.put(Type.countryCode, "at");
        expected.put(Type.country, "Austria");
        for (AddressComponent ac : addrComps) {
            String expValue = expected.remove(ac.getType());
            assertNotNull(expValue, "Unexpected " + ac);
            assertEquals(expValue, ac.getValue(), "AddressComponent");
        }
        assertThat(expected)
                .as("Missing expected AddressComponents " + expected)
                .isEmpty();

    }

    @Test
    void testReverseGeocode() throws IOException {
        final List<Place> places = gmGeocoder.reverseGeocode(latLon);

        assertThat(places)
                .as("places found")
                .hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void testLookup() throws IOException {
        final Place place = gmGeocoder.lookup(testPlaceId);

        assertThat(place)
                .as("Place Lookup")
                .hasFieldOrPropertyWithValue("placeId", testPlaceId)
                .hasFieldOrPropertyWithValue("address", testFormattedAddress);
    }

}

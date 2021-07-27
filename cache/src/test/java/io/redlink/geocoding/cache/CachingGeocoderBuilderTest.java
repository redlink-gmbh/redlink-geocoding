package io.redlink.geocoding.cache;

import io.redlink.geocoding.Geocoder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
class CachingGeocoderBuilderTest {

    @Test
    void testCreateWithoutDelegate() throws Exception {
        final CachingGeocoderBuilder builder = new CachingGeocoderBuilder();
        assertThatCode(builder::create)
                .as("Incomplete Builder")
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testCreate() {
        final Geocoder delegate = Mockito.mock(Geocoder.class);
        assertNotNull(new CachingGeocoderBuilder()
                .setGeocoder(delegate)
                .create(), "Caching Builder");
        assertNotNull(new CachingGeocoderBuilder(delegate)
                .create(), "Caching Builder");
    }

}
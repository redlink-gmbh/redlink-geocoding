package io.redlink.geocoding.nominatim;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
class NominatimBuilderTest {

    @Test
    void testCreate() {
        assertNotNull(new NominatimBuilder()
                .create(), "Nominatim Builder");
    }

    @Test
    void testWithIllegalURI() {
        final NominatimBuilder builder = new NominatimBuilder()
                .setBaseUrl("-- not a valid baseUrl --");
        assertThatCode(builder::create)
                .isInstanceOf(IllegalArgumentException.class)
        ;
    }
}
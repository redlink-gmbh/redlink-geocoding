package io.redlink.geocoding.nominatim;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 */
public class NominatimBuilderTest {

    @Test
    public void testCreate() throws Exception {
        assertNotNull(new NominatimBuilder()
                .create());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithIllegalURI() {
        new NominatimBuilder()
                .setBaseUrl("-- not a valid baseUrl --")
                .create();
        fail("Expected IllegalArgumentException");
    }
}
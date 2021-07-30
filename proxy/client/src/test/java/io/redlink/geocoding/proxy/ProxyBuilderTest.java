package io.redlink.geocoding.proxy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProxyBuilderTest {

    @Test
    void testCreate() {
        assertNotNull(new ProxyBuilder("https://example.com/")
                .create(), "Nominatim Builder");
    }

    @Test
    void testWithIllegalURI() {
        final ProxyBuilder builder = new ProxyBuilder();
        assertThatCode(builder::create)
                .as("No baseUrl set")
                .isInstanceOf(IllegalStateException.class);

        builder.setBaseUri("/foo/bar");
        assertThatCode(builder::create)
                .as("Relative baseUrl set")
                .isInstanceOf(IllegalArgumentException.class);
    }

}
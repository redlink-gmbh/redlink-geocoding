package io.redlink.geocoding.google;

import java.io.IOException;
import java.net.Proxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 *
 */
class GoogleMapsBuilderTest {

    @Mock
    private Proxy proxy;

    public GoogleMapsBuilderTest() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void init() {
        when(proxy.type()).thenReturn(Proxy.Type.DIRECT);
    }

    @Test
    void testCreate() throws IOException {
        assertNotNull(new GoogleMapsBuilder()
                .setApiKey("API key")
                .setChannel("channel")
                .setCredentials("client", "cryptoSecret")
                .setLocale("de")
                .setProxy(proxy)
                .create(), "Create GoogleMaps instance");
    }

    @Test
    void testWithoutCredentials() {
        final GoogleMapsBuilder builder = new GoogleMapsBuilder();
        assertThatCode(builder::create)
                .as("Incomplete Builder")
                .isInstanceOf(IllegalStateException.class);
    }
}

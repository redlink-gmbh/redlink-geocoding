package io.redlink.geocoding.google;

import java.net.Proxy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
class GoogleMapsBuilderTest {

    private final Proxy proxy;

    public GoogleMapsBuilderTest() {
        proxy = mock(Proxy.class);
        when(proxy.type()).thenReturn(Proxy.Type.DIRECT);
    }

    @Test
    void testCreate() {
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

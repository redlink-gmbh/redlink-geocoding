package io.redlink.geocoding.google;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.Proxy;

import static org.mockito.Mockito.when;

/**
 */
public class GoogleMapsBuilderTest {

    @Mock
    private Proxy proxy;

    public GoogleMapsBuilderTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void init() {
        when(proxy.type()).thenReturn(Proxy.Type.DIRECT);
    }

    @Test
    public void testCreate() throws IOException {
        Assert.assertNotNull(new GoogleMapsBuilder()
                .setApiKey("API key")
                .setChannel("channel")
                .setCredentials("client", "cryptoSecret")
                .setLocale("de")
                .setProxy(proxy)
                .create());
    }

    @Test(expected = IllegalStateException.class)
    public void testWithoutCredentials() {
        new GoogleMapsBuilder()
                .create();
        Assert.fail("Expected an IllegalStateException");
    }
}

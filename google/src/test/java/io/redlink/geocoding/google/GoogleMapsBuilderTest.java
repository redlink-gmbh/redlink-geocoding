package io.redlink.geocoding.google;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.Proxy;

import static org.mockito.Mockito.when;

/**
 * Created by fonso on 30.01.17.
 */
public class GoogleMapsBuilderTest {

    @Mock
    private Proxy proxy;

    private GoogleMapsBuilder gmBuilder;

    public GoogleMapsBuilderTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void init() {

        when(proxy.type()).thenReturn(Proxy.Type.DIRECT);

        gmBuilder = new GoogleMapsBuilder()
                .setApiKey("API key")
                .setChannel("channel")
                .setCredentials("client","cryptoSecret")
                .setLocale("de")
                .setProxy(proxy);
    }

    @Test
    public void testCreate() throws IOException {
        GoogleMapsGeocoder gmGeocoder = gmBuilder.create();



    }
}

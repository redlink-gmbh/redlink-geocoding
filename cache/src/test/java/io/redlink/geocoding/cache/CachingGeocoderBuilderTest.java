package io.redlink.geocoding.cache;

import io.redlink.geocoding.Geocoder;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 */
public class CachingGeocoderBuilderTest {

    @Test(expected = IllegalStateException.class)
    public void testCreateWithoutDelegate() throws Exception {
        new CachingGeocoderBuilder().create();
        fail("Expected IllegalStateException");
    }

    @Test
    public void testCreate() {
        Geocoder delegate = Mockito.mock(Geocoder.class);
        assertNotNull(new CachingGeocoderBuilder()
                .setGeocoder(delegate)
                .create());
        assertNotNull(new CachingGeocoderBuilder(delegate)
                .create());
    }

}
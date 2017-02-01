package io.redlink.geocoding.cache;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 */
public class CacheBuilderTest {

    @Test(expected = IllegalStateException.class)
    public void testCreateWithoutDelegate() throws Exception {
        new CacheBuilder().create();
        fail("Expected IllegalStateException");
    }

    @Test
    public void testCreate() {
        Geocoder delegate = Mockito.mock(Geocoder.class);
        assertNotNull(new CacheBuilder()
                .setGeocoder(delegate)
                .create());
        assertNotNull(new CacheBuilder(delegate)
                .create());
    }

}
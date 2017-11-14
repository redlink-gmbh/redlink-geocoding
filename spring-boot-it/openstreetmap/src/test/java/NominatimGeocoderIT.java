/*
 * Copyright (c) 2017 Redlink GmbH.
 */

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.nominatim.NominatimGeocoder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@EnableAutoConfiguration
public class NominatimGeocoderIT {

    @Autowired
    private Geocoder geocoder;

    @Test
    public void testInject() {
        Assert.assertNotNull(geocoder);
    }

    @Test
    public void testType() throws Exception {
        Assert.assertThat(geocoder, Matchers.instanceOf(NominatimGeocoder.class));
    }
}

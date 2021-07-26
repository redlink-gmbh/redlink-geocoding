/*
 * Copyright (c) 2017 Redlink GmbH.
 */

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.nominatim.NominatimGeocoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("nominatim")
@EnableAutoConfiguration
class NominatimGeocoderTest {

    private final Geocoder geocoder;

    @Autowired
    NominatimGeocoderTest(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    @Test
    void testInject() {
        assertNotNull(geocoder, "Geocoder-Bean missing");
    }

    @Test
    void testType() throws Exception {
        assertThat(geocoder)
                .as("NominatimGeocoder expected")
                .isInstanceOf(NominatimGeocoder.class);
    }
}

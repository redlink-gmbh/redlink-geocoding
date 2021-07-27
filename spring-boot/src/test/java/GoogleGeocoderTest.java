/*
 * Copyright (c) 2017 Redlink GmbH.
 */

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.google.GoogleMapsGeocoder;
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
@ActiveProfiles("google")
@EnableAutoConfiguration
class GoogleGeocoderTest {

    private final Geocoder geocoder;

    @Autowired
    GoogleGeocoderTest(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    @Test
    void testInject() {
        assertNotNull(geocoder, "Geocoder-Bean missing");
    }

    @Test
    void testType() throws Exception {
        assertThat(geocoder)
                .as("GoogleGeocoder expected")
                .isInstanceOf(GoogleMapsGeocoder.class);
    }
}

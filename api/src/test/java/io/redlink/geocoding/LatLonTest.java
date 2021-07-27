package io.redlink.geocoding;

import java.util.Random;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 *
 */
class LatLonTest {
    private final LatLon latLon1 = new LatLon(45.000, 12.000);
    private final LatLon latLon2 = new LatLon(45.000, 12.000);
    private final LatLon latLon3 = new LatLon(43.0204, 15.010);
    private final LatLon latLon4 = new LatLon(40.10006, 13.000);

    @Test
    void testEqual() {
        assertEquals(latLon2, latLon1, "one equals two");
        assertNotEquals(latLon3, latLon1, "one equals three");
        assertNotEquals(latLon4, latLon1, "one equals four");
        assertNotEquals(latLon3, latLon2, "two equals three");
        assertNotEquals(latLon4, latLon2, "two equals four");
        assertNotEquals(latLon4, latLon3, "three equals four");

    }

    @Test
    void testHashCode() {
        assertThat(latLon1)
                .as("one and two")
                .hasSameHashCodeAs(latLon2);

        assertThat(latLon3)
                .as("three and two")
                .doesNotHaveSameHashCodeAs(latLon2);
        assertThat(latLon4)
                .as("four and two")
                .doesNotHaveSameHashCodeAs(latLon2);
    }

    @Test
    void testValueOf() {
        final Random rnd = new Random();
        final double lat = -90d + 180d * rnd.nextDouble(),
                lon = -180d + 360d * rnd.nextDouble();
        final LatLon expected = new LatLon(lat, lon);

        assertEquals(expected, LatLon.valueOf(lat + "," + lon), "parsing from string");
        assertEquals(expected, LatLon.valueOf(String.valueOf(lat), String.valueOf(lon)), "parsing from strings");
    }
}

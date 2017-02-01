package io.redlink.geocoding;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;
import java.util.Random;

/**
 */
public class LatLonTest {
    private final LatLon latLon1 = new LatLon(45.000, 12.000);
    private final LatLon latLon2 = new LatLon(45.000, 12.000);
    private final LatLon latLon3 = new LatLon(43.0204, 15.010);
    private final LatLon latLon4 = new LatLon(40.10006, 13.000);

    @Test
    public void testEqual() {
        Assert.assertEquals(latLon1, latLon2);
        Assert.assertNotEquals(latLon1, latLon3);
        Assert.assertNotEquals(latLon1, latLon4);
        Assert.assertNotEquals(latLon2, latLon3);
        Assert.assertNotEquals(latLon2, latLon4);
        Assert.assertNotEquals(latLon3, latLon4);

    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(latLon1.hashCode(), latLon2.hashCode());
        Assert.assertNotEquals(latLon1.hashCode(), latLon3.hashCode());
        Assert.assertNotEquals(latLon1.hashCode(), latLon4.hashCode());
        Assert.assertNotEquals(latLon2.hashCode(), latLon3.hashCode());
        Assert.assertNotEquals(latLon2.hashCode(), latLon4.hashCode());
        Assert.assertNotEquals(latLon3.hashCode(), latLon4.hashCode());
    }

    @Test
    public void testValueOf() {
        final Random rnd = new Random();
        final double lat = -90d + 180d * rnd.nextDouble(),
                lon = -180d + 360d * rnd.nextDouble();
        final LatLon expected = new LatLon(lat, lon);

        Assert.assertEquals(expected, LatLon.valueOf(String.valueOf(lat) + "," + String.valueOf(lon)));
        Assert.assertEquals(expected, LatLon.valueOf(String.valueOf(lat), String.valueOf(lon)));
    }
}

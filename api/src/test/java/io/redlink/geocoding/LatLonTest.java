package io.redlink.geocoding;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fonso on 31.01.17.
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
}

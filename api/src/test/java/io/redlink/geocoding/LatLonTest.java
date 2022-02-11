/*
 * Copyright (c) 2022 Redlink GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    private final LatLon latLon1 = LatLon.create(45.000, 12.000);
    private final LatLon latLon2 = LatLon.create(45.000, 12.000);
    private final LatLon latLon3 = LatLon.create(43.0204, 15.010);
    private final LatLon latLon4 = LatLon.create(40.10006, 13.000);

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
        final LatLon expected = LatLon.create(lat, lon);

        assertEquals(expected, LatLon.valueOf(lat + "," + lon), "parsing from string");
        assertEquals(expected, LatLon.valueOf(String.valueOf(lat), String.valueOf(lon)), "parsing from strings");
    }
}

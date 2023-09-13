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

package io.redlink.geocoding.cache;

import com.google.common.base.Preconditions;
import io.redlink.geocoding.Geocoder;
import java.util.concurrent.TimeUnit;

/**
 * A builder for CacheGeocoder.
 *
 */
public class CachingGeocoderBuilder {

    private long cacheExpireTime = CachingGeocoder.DEFAULT_CACHE_EXPIRE_TIME;
    private TimeUnit timeUnit = CachingGeocoder.DEFAULT_TIME_UNIT;
    private Geocoder geocoder = null;

    /**
     * @deprecated use {@link CachingGeocoder#builder()}
     */
    @Deprecated(since = "2.0.2")
    public CachingGeocoderBuilder() {
    }

    /**
     * @deprecated use {@link CachingGeocoder#builder()}
     */
    @Deprecated(since = "2.0.2")
    public CachingGeocoderBuilder(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    public CachingGeocoderBuilder setCacheExpiry(long timeout, TimeUnit timeUnit) {
        this.cacheExpireTime = timeout;
        this.timeUnit = timeUnit;
        return this;
    }

    public CachingGeocoderBuilder setGeocoder(Geocoder geocoder) {
        this.geocoder = geocoder;
        return this;
    }

    public CachingGeocoder create() {
        Preconditions.checkState(geocoder != null, "geocoder must be set!");
        return new CachingGeocoder(geocoder, cacheExpireTime, timeUnit);
    }
}

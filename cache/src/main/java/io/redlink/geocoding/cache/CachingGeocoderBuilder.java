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

    public CachingGeocoderBuilder() {
    }

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

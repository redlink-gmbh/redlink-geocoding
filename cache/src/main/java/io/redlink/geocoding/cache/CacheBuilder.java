package io.redlink.geocoding.cache;

import com.google.common.base.Preconditions;
import io.redlink.geocoding.Geocoder;

import java.util.concurrent.TimeUnit;

/**
 * A builder for CacheGeocoder.
 *
 */
public class CacheBuilder {

    private long cacheExpireTime = CacheGeocoder.DEFAULT_CACHE_EXPIRE_TIME;
    private TimeUnit timeUnit = CacheGeocoder.DEFAULT_TIME_UNIT;
    private Geocoder geocoder = null;

    public CacheBuilder() {
    }

    public CacheBuilder(Geocoder geocoder) {
        setGeocoder(geocoder);
    }

    public CacheBuilder setCacheExpiry(long timeout, TimeUnit timeUnit) {
        this.cacheExpireTime = timeout;
        this.timeUnit = timeUnit;
        return this;
    }

    public CacheBuilder setGeocoder(Geocoder geocoder) {
        this.geocoder = geocoder;
        return this;
    }

    public CacheGeocoder create() {
        Preconditions.checkState(geocoder != null, "gecoder must be set!");
        return new CacheGeocoder(geocoder, cacheExpireTime,timeUnit);
    }
}

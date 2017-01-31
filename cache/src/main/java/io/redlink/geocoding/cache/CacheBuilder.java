package io.redlink.geocoding.cache;

import io.redlink.geocoding.Geocoder;
import org.apache.commons.lang3.Validate;

import java.util.concurrent.TimeUnit;

/**
 * A builder for CacheGeocoder.
 *
 */
public class CacheBuilder {

    private long cacheExpireTime;
    private TimeUnit timeUnit;
    private Geocoder geocoder;

    public CacheBuilder(Geocoder geocoder) {
        Validate.notNull(geocoder, "A non-null Geocoder should be provided.");
        this.geocoder = geocoder;
    }

    public CacheBuilder setCacheExpireTime(long cacheExpireTime) {
        this.cacheExpireTime = cacheExpireTime;
        return this;
    }

    public CacheBuilder setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public CacheBuilder setGeocoder(Geocoder geocoder) {
        this.geocoder = geocoder;
        return this;
    }

    public CacheGeocoder create() {
        return new CacheGeocoder(geocoder, cacheExpireTime,timeUnit);
    }
}

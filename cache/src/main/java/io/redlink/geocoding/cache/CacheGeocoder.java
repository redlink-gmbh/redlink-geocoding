package io.redlink.geocoding.cache;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Alfonso Noriega
 */
public class CacheGeocoder implements Geocoder {

    private static final Logger log = LoggerFactory.getLogger(CacheGeocoder.class);
    public static final int DEFAULT_CACHE_EXPIRE_TIME = 24;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.HOURS;

    private final Geocoder geocoder;

    private final LoadingCache<String, List<Place>> geocodeCache;
    private final LoadingCache<LatLon, List<Place>> reverseGeocodeCache;
    private final LoadingCache<String, Place> lookupCache;

    protected CacheGeocoder(Geocoder geocoder) {
      this(geocoder, DEFAULT_CACHE_EXPIRE_TIME, DEFAULT_TIME_UNIT);
    }

    protected CacheGeocoder(Geocoder geocoder, long cacheExpireTime, TimeUnit timeUnit) {
        Preconditions.checkArgument(geocoder != null, "Geocoder must not be null");
        Preconditions.checkNotNull(timeUnit);

        this.geocoder = geocoder;

        geocodeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheExpireTime, timeUnit)
                .build(new CacheLoader<String, List<Place>>() {
                    @Override
                    public List<Place> load(String s) throws Exception {
                        return CacheGeocoder.this.geocoder.geocode(s);
                    }
                });

        reverseGeocodeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheExpireTime, timeUnit)
                .build(new CacheLoader<LatLon, List<Place>>() {
                    @Override
                    public List<Place> load(LatLon coordinates) throws Exception {
                        return CacheGeocoder.this.geocoder.reverseGeocode(coordinates);
                    }
                });

        lookupCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheExpireTime, timeUnit)
                .build(new CacheLoader<String,Place>() {
                    @Override
                    public Place load(String s) throws Exception {
                        return CacheGeocoder.this.geocoder.lookup(s);
                    }
                });
    }

    @Override
    public List<Place> geocode(String address) throws IOException {
        try {
            return geocodeCache.get(address);
        } catch (ExecutionException e) {
            log.error("Cache geo-coding service client unable to retrieve data with query '{}': {}", address, e.getMessage(), e);
            throw new IOException("Error loading geocodeCache for '" + address + "'", e);
        }
    }

    @Override
    public List<Place> reverseGeocode(LatLon coordinates) throws IOException {
        try {
            return reverseGeocodeCache.get(coordinates);
        } catch (ExecutionException e) {
            log.error("Cache reverse geo-coding service client unable to retrieve data with lat,long '{},{}': {}",
                    coordinates.lat(),coordinates.lon(), e.getMessage(), e);
            throw new IOException("Error loading reverseGeocodeCache for '" + coordinates + "'", e);
        }
    }

    @Override
    public Place lookup(String placeId) throws IOException {
        try {
            return lookupCache.get(placeId);
        } catch (ExecutionException e) {
            log.error("Cache lookup service client unable to retrieve data with palceId '{}': {}", placeId, e.getMessage(), e);
            throw new IOException("Error loading lookupCache for '" + placeId + "'", e);
        }
    }

    public static io.redlink.geocoding.cache.CacheBuilder configure() {
        return new io.redlink.geocoding.cache.CacheBuilder();
    }

}

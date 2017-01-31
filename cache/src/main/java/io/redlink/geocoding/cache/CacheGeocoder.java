package io.redlink.geocoding.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by fonso on 31.01.17.
 */
public class CacheGeocoder implements Geocoder {

    private static final Logger log = LoggerFactory.getLogger(CacheGeocoder.class);
    public static final int DEFAULT_CACHE_EXPIRE_TIME = 7;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.DAYS;

    private long expireTime;
    private TimeUnit timeUnit;

    private final Geocoder geocoder;

    private LoadingCache<String, List<Place>> geocodeCache;
    private LoadingCache<LatLon, List<Place>> reverseGeocodeCache;
    private LoadingCache<String, Place> lookupCache;

    protected CacheGeocoder(Geocoder geocoder) {
      this(geocoder, DEFAULT_CACHE_EXPIRE_TIME, DEFAULT_TIME_UNIT);
    }

    protected CacheGeocoder(Geocoder geocoder, long cacheExpireTime, TimeUnit timeUnit) {
        this.geocoder = geocoder;
        this.expireTime = cacheExpireTime;
        this.timeUnit = timeUnit;

        this.prepareCaches();
    }

    @Override
    public List<Place> geocode(String address) throws IOException {
        try {
            List<Place> places = geocodeCache.get(address);
            if (places.isEmpty()){
                geocodeCache.invalidate(address);
            }
            return places;
        } catch (ExecutionException e) {
            log.error("Cache geo-coding service client unable to retrieve data with query '{}': {}", address, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Place> reverseGeocode(LatLon coordinates) throws IOException {
        try {
            List<Place> places = reverseGeocodeCache.get(coordinates);
            if (places.isEmpty()){
                reverseGeocodeCache.invalidate(coordinates);
            }
            return places;
        } catch (ExecutionException e) {
            log.error("Cache reverse geo-coding service client unable to retrieve data with lat,long '{},{}': {}",
                    coordinates.lat(),coordinates.lon(), e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Place lookup(String placeId) throws IOException {
        try {
            Place place = lookupCache.get(placeId);
            if (place == null){
                lookupCache.invalidate(placeId);
            }
            return place;
        } catch (ExecutionException e) {
            log.error("Cache lookup service client unable to retrieve data with palceId '{}': {}", placeId, e.getMessage(), e);
            return null;
        }
    }

    private void prepareCaches() {

        geocodeCache = CacheBuilder.newBuilder()
                .expireAfterWrite( this.expireTime, this.timeUnit)
                .build(new CacheLoader<String, List<Place>>() {
                    @Override
                    public List<Place> load(String s) throws Exception {
                        return doGeocode(s);
                    }
                });

        reverseGeocodeCache = CacheBuilder.newBuilder()
                .expireAfterWrite( this.expireTime,  this.timeUnit)
                .build(new CacheLoader<LatLon, List<Place>>() {
                    @Override
                    public List<Place> load(LatLon coordinates) throws Exception {
                        return doReverseGeocode(coordinates);
                    }
                });

        lookupCache = CacheBuilder.newBuilder()
                .expireAfterWrite( this.expireTime,  this.timeUnit)
                .build(new CacheLoader<String,Place>() {
                    @Override
                    public Place load(String s) throws Exception {
                        return doLookup(s);
                    }
                });
    }

    private List<Place> doGeocode(String address) throws IOException {
        return this.geocoder.geocode(address);
    }

    private List<Place> doReverseGeocode(LatLon coordinates) throws IOException {
        return this.geocoder.reverseGeocode(coordinates);
    }

    private Place doLookup(String placeId) throws IOException {
        return this.geocoder.lookup(placeId);
    }
}

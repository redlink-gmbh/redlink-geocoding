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
public class CachingGeocoder implements Geocoder {

    private static final Logger log = LoggerFactory.getLogger(CachingGeocoder.class);
    public static final int DEFAULT_CACHE_EXPIRE_TIME = 24;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.HOURS;

    private final Geocoder geocoder;

    private final LoadingCache<LangString, List<Place>> geocodeCache;
    private final LoadingCache<LatLon, List<Place>> reverseGeocodeCache;
    private final LoadingCache<String, Place> lookupCache;

    private final String cacheExpiry;

    protected CachingGeocoder(Geocoder geocoder) {
      this(geocoder, DEFAULT_CACHE_EXPIRE_TIME, DEFAULT_TIME_UNIT);
    }

    protected CachingGeocoder(Geocoder geocoder, long cacheExpireTime, TimeUnit timeUnit) {
        Preconditions.checkArgument(geocoder != null, "Geocoder must not be null");
        Preconditions.checkNotNull(timeUnit);

        this.geocoder = geocoder;
        this.cacheExpiry = String.format("%d %s", cacheExpireTime, timeUnit);

        geocodeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheExpireTime, timeUnit)
                .build(new CacheLoader<LangString, List<Place>>() {
                    @Override
                    public List<Place> load(LangString s) throws Exception {
                        return CachingGeocoder.this.geocoder.geocode(s.value,s.lang);
                    }
                });

        reverseGeocodeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheExpireTime, timeUnit)
                .build(new CacheLoader<LatLon, List<Place>>() {
                    @Override
                    public List<Place> load(LatLon coordinates) throws Exception {
                        return CachingGeocoder.this.geocoder.reverseGeocode(coordinates);
                    }
                });

        lookupCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheExpireTime, timeUnit)
                .build(new CacheLoader<String,Place>() {
                    @Override
                    public Place load(String s) throws Exception {
                        return CachingGeocoder.this.geocoder.lookup(s);
                    }
                });
    }

    @Override
    public List<Place> geocode(String address, String lang) throws IOException {
        try {
            return geocodeCache.get(new LangString(address, lang));
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
    
    

    @Override
    public String toString() {
        return "CachingGeocoder [geocoder=" + geocoder + ", expires=" + cacheExpiry + "]";
    }

    public static CachingGeocoderBuilder configure() {
        return new CachingGeocoderBuilder();
    }

    private static class LangString {
        
        final String lang;
        final String value;
        
        public LangString(String value, String lang) {
            this.lang = lang != null && lang.isEmpty() ? null : lang;
            this.value = value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((lang == null) ? 0 : lang.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            LangString other = (LangString) obj;
            if (lang == null) {
                if (other.lang != null)
                    return false;
            } else if (!lang.equals(other.lang))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }
    }
    
    
}

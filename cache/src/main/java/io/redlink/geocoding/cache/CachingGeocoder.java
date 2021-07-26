package io.redlink.geocoding.cache;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.Place;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alfonso Noriega
 */
public class CachingGeocoder implements Geocoder {

    private static final Logger LOG = LoggerFactory.getLogger(CachingGeocoder.class);

    public static final int DEFAULT_CACHE_EXPIRE_TIME = 24;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.HOURS;

    private final Geocoder geocoder;

    private final LoadingCache<LangString, List<Place>> geocodeCache;
    private final LoadingCache<LangCoords, List<Place>> reverseGeocodeCache;
    private final LoadingCache<LangString, Place> lookupCache;

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
                        return CachingGeocoder.this.geocoder.geocode(s.value, s.lang);
                    }
                });

        reverseGeocodeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheExpireTime, timeUnit)
                .build(new CacheLoader<LangCoords, List<Place>>() {
                    @Override
                    public List<Place> load(LangCoords coordinates) throws Exception {
                        return CachingGeocoder.this.geocoder.reverseGeocode(coordinates.coords, coordinates.lang);
                    }
                });

        lookupCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheExpireTime, timeUnit)
                .build(new CacheLoader<LangString, Place>() {
                    @Override
                    public Place load(LangString s) throws Exception {
                        return CachingGeocoder.this.geocoder.lookup(s.value, s.lang);
                    }
                });
    }

    @Override
    public List<Place> geocode(String address, Locale lang) throws IOException {
        try {
            LOG.debug("Geocoding '{}'", address);
            return geocodeCache.get(new LangString(address, lang));
        } catch (ExecutionException e) {
            LOG.error("Cache geo-coding service client unable to retrieve data with query '{}': {}", address, e.getMessage(), e);
            throw new IOException("Error loading geocodeCache for '" + address + "'", e);
        }
    }

    @Override
    public List<Place> reverseGeocode(LatLon coordinates, Locale lang) throws IOException {
        try {
            LOG.debug("Reverse-Geocoding '{}'", coordinates);
            return reverseGeocodeCache.get(new LangCoords(coordinates, lang));
        } catch (ExecutionException e) {
            LOG.error("Cache reverse geo-coding service client unable to retrieve data with lat,long '{},{}': {}",
                    coordinates.lat(), coordinates.lon(), e.getMessage(), e);
            throw new IOException("Error loading reverseGeocodeCache for '" + coordinates + "'", e);
        }
    }

    @Override
    public Place lookup(String placeId, Locale lang) throws IOException {
        try {
            LOG.debug("Lookup of '{}'", placeId);
            return lookupCache.get(new LangString(placeId, lang));
        } catch (ExecutionException e) {
            LOG.error("Cache lookup service client unable to retrieve data with placeId '{}': {}", placeId, e.getMessage(), e);
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

        final Locale lang;
        final String value;

        LangString(String value, Locale lang) {
            this.lang = lang;
            this.value = value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, lang);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof LangString)) return false;
            LangString that = (LangString) obj;
            return Objects.equals(lang, that.lang) &&
                    Objects.equals(value, that.value);
        }

        @Override
        public String toString() {
            return "LangString{" +
                    "lang=" + lang +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    private static class LangCoords {
        final Locale lang;
        final LatLon coords;

        LangCoords(LatLon coords, Locale lang) {
            this.lang = lang;
            this.coords = coords;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LangCoords)) return false;
            LangCoords that = (LangCoords) o;
            return Objects.equals(lang, that.lang) &&
                    Objects.equals(coords, that.coords);
        }

        @Override
        public int hashCode() {
            return Objects.hash(lang, coords);
        }

        @Override
        public String toString() {
            return "LangCoords{" +
                    "lang=" + lang +
                    ", coords=" + coords +
                    '}';
        }
    }


}

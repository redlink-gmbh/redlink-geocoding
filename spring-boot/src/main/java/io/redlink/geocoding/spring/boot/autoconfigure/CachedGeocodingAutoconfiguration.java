/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.spring.boot.autoconfigure;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.cache.CacheGeocoder;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.concurrent.TimeUnit;

/**
 */
@Configuration
@ConditionalOnClass(CacheGeocoder.class)
@ConditionalOnBean(Geocoder.class)
@EnableConfigurationProperties(GeocodingProperties.class)
@AutoConfigureAfter(GeocodingAutoConfiguration.class)
public class CachedGeocodingAutoconfiguration {

    private final GeocodingProperties properties;
    private final Geocoder geocoder;

    public CachedGeocodingAutoconfiguration(GeocodingProperties properties, Geocoder geocoder) {
        this.properties = properties;
        this.geocoder = geocoder;
    }

    @Primary
    @Bean("cachedGeocoder")
    @Conditional(CacheConfigurationCondition.class)
    public CacheGeocoder cacheGeocoder() {
        return CacheGeocoder.configure()
                .setCacheExpiry(properties.getCacheTimeout(), TimeUnit.SECONDS)
                .setGeocoder(geocoder)
                .create();
    }

    private static class CacheConfigurationCondition implements ConfigurationCondition {
        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return ConfigurationPhase.REGISTER_BEAN;
        }

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return context.getEnvironment().getProperty("geocoding.cache-timeout", Long.class, -1l) > 0;
        }
    }
}

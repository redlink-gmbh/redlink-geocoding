/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.spring.boot.autoconfigure;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.cache.CachingGeocoder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
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
@ConditionalOnClass(CachingGeocoder.class)
@ConditionalOnBean(Geocoder.class)
@EnableConfigurationProperties(GeocodingProperties.class)
@Conditional(CachedGeocodingAutoconfiguration.CacheConfigurationCondition.class)
@AutoConfigureAfter({GoogleGeocodingAutoConfiguration.class, NominatimGeocodingAutoConfiguration.class})
public class CachedGeocodingAutoconfiguration extends GeocodingAutoConfiguration {

    private final Geocoder geocoder;

    public CachedGeocodingAutoconfiguration(GeocodingProperties properties, Geocoder geocoder) {
        super(properties);
        this.geocoder = geocoder;
    }

    @Primary
    @Bean("cachedGeocoder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CachingGeocoder cacheGeocoder() {
        return CachingGeocoder.configure()
                .setCacheExpiry(properties.getCacheTimeout(), TimeUnit.SECONDS)
                .setGeocoder(geocoder)
                .create();
    }

    static class CacheConfigurationCondition implements ConfigurationCondition {
        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return ConfigurationPhase.REGISTER_BEAN;
        }

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return context.getEnvironment().getProperty("geocoding.cache-timeout", Long.class, -1L) > 0;
        }
    }
}
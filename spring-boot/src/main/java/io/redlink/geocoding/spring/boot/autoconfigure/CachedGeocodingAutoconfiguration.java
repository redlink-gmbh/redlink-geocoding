/*
 * Copyright (c) 2017-2022 Redlink GmbH.
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
package io.redlink.geocoding.spring.boot.autoconfigure;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.cache.CachingGeocoder;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 */
@Configuration
@ConditionalOnClass(CachingGeocoder.class)
@ConditionalOnBean(Geocoder.class)
@EnableConfigurationProperties(GeocodingProperties.class)
@Conditional(CachedGeocodingAutoconfiguration.CacheConfigurationCondition.class)
@AutoConfigureAfter({
        GoogleGeocodingAutoConfiguration.class,
        NominatimGeocodingAutoConfiguration.class,
        ProxyGeocodingAutoConfiguration.class,
})
public class CachedGeocodingAutoconfiguration extends GeocodingAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(CachedGeocodingAutoconfiguration.class);

    private final Geocoder geocoder;

    public CachedGeocodingAutoconfiguration(GeocodingProperties properties, Geocoder geocoder) {
        super(properties);
        this.geocoder = geocoder;
    }

    @Primary
    @Bean("cachedGeocoder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CachingGeocoder cacheGeocoder() {
        final CachingGeocoder cachingGeocoder = CachingGeocoder.builder()
                .setCacheExpiry(properties.getCacheTimeout(), TimeUnit.SECONDS)
                .setGeocoder(geocoder)
                .create();
        LOG.info("Initializing {}", cachingGeocoder);
        return cachingGeocoder;
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

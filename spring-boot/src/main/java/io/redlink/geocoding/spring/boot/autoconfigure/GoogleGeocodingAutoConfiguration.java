/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.spring.boot.autoconfigure;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.google.GoogleMapsBuilder;
import io.redlink.geocoding.google.GoogleMapsGeocoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 */
@Configuration
@ConditionalOnMissingBean(Geocoder.class)
@ConditionalOnClass(GoogleMapsGeocoder.class)
@Conditional(GoogleGeocodingAutoConfiguration.GoogleMapsCondition.class)
@EnableConfigurationProperties(GeocodingProperties.class)
public class GoogleGeocodingAutoConfiguration extends GeocodingAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleGeocodingAutoConfiguration.class);

    public GoogleGeocodingAutoConfiguration(GeocodingProperties properties) {
        super(properties);
    }

    @Bean(name = "googleGeocoder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Geocoder google() {
        final GeocodingProperties.GoogleMapsProperties google = properties.getGoogle();

        final GoogleMapsBuilder googleMapsBuilder = GoogleMapsGeocoder.configure();
        if (StringUtils.isNotBlank(google.getApiKey())) {
            googleMapsBuilder.setApiKey(google.getApiKey());
        }

        if (StringUtils.isNotBlank(google.getChannel())) {
            googleMapsBuilder.setChannel(google.getChannel());
        }

        if (StringUtils.isNoneBlank(google.getClientId(), google.getCryptoSecret())) {
            googleMapsBuilder.setCredentials(google.getClientId(), google.getCryptoSecret());
        }

        final GoogleMapsGeocoder googleMapsGeocoder = googleMapsBuilder
                .setLocale(properties.getLang())
                .setProxy(buildProxy())
                .create();
        LOG.info("Initializing {}", googleMapsGeocoder);
        return googleMapsGeocoder;
    }

    static class GoogleMapsCondition implements ConfigurationCondition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            final Environment env = context.getEnvironment();
            return StringUtils.isNotBlank(env.getProperty("geocoding.google.api-key")) ||
                    StringUtils.isNoneBlank(env.getProperty("geocoding.google.client-id"), env.getProperty("geocoding.google.crypto-secret"));
        }

        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return ConfigurationPhase.REGISTER_BEAN;
        }
    }
}

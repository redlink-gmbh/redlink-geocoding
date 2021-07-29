/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.spring.boot.autoconfigure;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.proxy.ProxyBuilder;
import io.redlink.geocoding.proxy.ProxyGeocoder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
@ConditionalOnClass(ProxyGeocoder.class)
@ConditionalOnMissingBean(Geocoder.class)
@Conditional(ProxyGeocodingAutoConfiguration.ProxyGeocoderCondition.class)
@EnableConfigurationProperties(GeocodingProperties.class)
@AutoConfigureAfter({NominatimGeocodingAutoConfiguration.class})
public class ProxyGeocodingAutoConfiguration extends GeocodingAutoConfiguration {

    public ProxyGeocodingAutoConfiguration(GeocodingProperties properties) {
        super(properties);
    }

    @Bean(name = "proxyGeocoder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Geocoder nominatim() {
        final GeocodingProperties.ProxyProperties proxyProperties = properties.getProxyService();

        final ProxyBuilder proxyBuilder = ProxyBuilder.configure()
                    .setBaseUri(proxyProperties.getBaseUrl());

        return proxyBuilder
                .setLocale(properties.getLang())
                .create();
    }

    static class ProxyGeocoderCondition implements ConfigurationCondition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            final Environment env = context.getEnvironment();
            return StringUtils.isNotBlank(env.getProperty("geocoding.proxy-service.base-url"));
        }

        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return ConfigurationPhase.REGISTER_BEAN;
        }
    }

}

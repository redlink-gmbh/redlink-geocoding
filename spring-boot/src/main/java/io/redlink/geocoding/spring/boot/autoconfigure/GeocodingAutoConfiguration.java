/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.spring.boot.autoconfigure;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.google.GoogleMapsBuilder;
import io.redlink.geocoding.google.GoogleMapsGeocoder;
import io.redlink.geocoding.nominatim.NominatimBuilder;
import io.redlink.geocoding.nominatim.NominatimGeocoder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 */
@Configuration
@ConditionalOnClass(Geocoder.class)
@EnableConfigurationProperties(GeocodingProperties.class)
public class GeocodingAutoConfiguration {

    private final GeocodingProperties properties;

    public GeocodingAutoConfiguration(GeocodingProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "googleGeocoder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @ConditionalOnMissingBean(Geocoder.class)
    @ConditionalOnClass(GoogleMapsGeocoder.class)
    @Conditional(GoogleMapsCondition.class)
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

        return googleMapsBuilder
                .setLocale(properties.getLang())
                .setProxy(buildProxy())
                .create();
    }

    @Bean(name = "nominatimGeocoder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @ConditionalOnClass(NominatimGeocoder.class)
    @ConditionalOnMissingBean(Geocoder.class)
    public Geocoder nominatim() {
        final GeocodingProperties.NominatimProperties nominatim = properties.getNominatim();

        final NominatimBuilder nominatimBuilder = NominatimGeocoder.configure()
                    .setEmail(nominatim.getEmail());

        if (nominatim.getBaseUrl() != null) {
            nominatimBuilder.setBaseUrl(nominatim.getBaseUrl());
        }

        return nominatimBuilder
                .setLocale(properties.getLang())
                .setProxy(buildProxy())
                .create();
    }

    private Proxy buildProxy() {
        final URL url = properties.getProxy();
        if (url == null) return Proxy.NO_PROXY;

        if ("http".equals(url.getProtocol())) {
            int port = url.getPort();
            if (port < 0) port = url.getDefaultPort();
            if (port < 0) port = 80;

            return new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(url.getHost(), port));
        } else {
            throw new IllegalArgumentException("Proxy-Protocol " + url.getProtocol() + " not supported.");
        }
    }

    private static class GoogleMapsCondition implements ConfigurationCondition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            final Environment env = context.getEnvironment();
            return StringUtils.isNotBlank(env.getProperty("geocoding.google.api-key")) ||
                    StringUtils.isNoneBlank(env.getProperty("geocoding.google.client-id"),env.getProperty("geocoding.google.crypto-secret"));
        }

        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return ConfigurationPhase.REGISTER_BEAN;
        }
    }
}

/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package io.redlink.geocoding.spring.boot.autoconfigure;

import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.nominatim.NominatimBuilder;
import io.redlink.geocoding.nominatim.NominatimGeocoder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 */
@Configuration
@ConditionalOnClass(NominatimGeocoder.class)
@ConditionalOnMissingBean(Geocoder.class)
@EnableConfigurationProperties(GeocodingProperties.class)
@AutoConfigureAfter({GoogleGeocodingAutoConfiguration.class})
public class NominatimGeocodingAutoConfiguration extends GeocodingAutoConfiguration {

    public NominatimGeocodingAutoConfiguration(GeocodingProperties properties) {
        super(properties);
    }

    @Bean(name = "nominatimGeocoder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
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

}

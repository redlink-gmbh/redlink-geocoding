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
import io.redlink.geocoding.nominatim.NominatimBuilder;
import io.redlink.geocoding.nominatim.NominatimGeocoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(NominatimGeocodingAutoConfiguration.class);

    public NominatimGeocodingAutoConfiguration(GeocodingProperties properties) {
        super(properties);
    }

    @Bean(name = "nominatimGeocoder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Geocoder nominatim() {
        final GeocodingProperties.NominatimProperties nominatim = properties.getNominatim();

        final NominatimBuilder nominatimBuilder = NominatimGeocoder.builder()
                    .setEmail(nominatim.getEmail());

        if (nominatim.getBaseUrl() != null) {
            nominatimBuilder.setBaseUrl(nominatim.getBaseUrl());
        }

        if (nominatim.getEndpoints().getGeocoding() != null) {
            nominatimBuilder.setGeocodeEndpoint(nominatim.getEndpoints().getGeocoding());
        }
        if (nominatim.getEndpoints().getReverse() != null) {
            nominatimBuilder.setReverseEndpoint(nominatim.getEndpoints().getReverse());
        }
        if (nominatim.getEndpoints().getLookup() != null) {
            nominatimBuilder.setLookupEndpoint(nominatim.getEndpoints().getLookup());
        }

        if (nominatim.getExtraQueryParams() != null) {
            nominatim.getExtraQueryParams().forEach(nominatimBuilder::setStaticQueryParam);
        }
        if (nominatim.getExtraHeaders() != null) {
            nominatim.getExtraHeaders().forEach(nominatimBuilder::setStaticHeader);
        }

        final NominatimGeocoder nominatimGeocoder = nominatimBuilder
                .setLocale(properties.getLang())
                .setProxy(buildProxy())
                .create();
        LOG.info("Initializing {}", nominatimGeocoder);
        return nominatimGeocoder;
    }

}

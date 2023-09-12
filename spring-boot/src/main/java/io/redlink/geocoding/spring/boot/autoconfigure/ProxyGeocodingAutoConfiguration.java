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
import io.redlink.geocoding.proxy.ProxyBuilder;
import io.redlink.geocoding.proxy.ProxyGeocoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@AutoConfigureAfter({GoogleGeocodingAutoConfiguration.class, NominatimGeocodingAutoConfiguration.class})
public class ProxyGeocodingAutoConfiguration extends GeocodingAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyGeocodingAutoConfiguration.class);

    public ProxyGeocodingAutoConfiguration(GeocodingProperties properties) {
        super(properties);
    }

    @Bean(name = "proxyGeocoder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Geocoder nominatim() {
        final GeocodingProperties.ProxyProperties proxyProperties = properties.getProxyService();

        final ProxyBuilder proxyBuilder = ProxyGeocoder.configure()
                    .setBaseUri(proxyProperties.getBaseUrl());

        final ProxyGeocoder proxyGeocoder = proxyBuilder
                .setLocale(properties.getLang())
                .create();
        LOG.info("Initializing {}", proxyGeocoder);
        return proxyGeocoder;
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

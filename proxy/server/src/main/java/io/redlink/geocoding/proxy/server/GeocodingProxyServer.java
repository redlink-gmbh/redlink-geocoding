/*
 * Copyright (c) 2021-2022 Redlink GmbH.
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
package io.redlink.geocoding.proxy.server;

import io.redlink.geocoding.spring.boot.autoconfigure.ProxyGeocodingAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {
        ProxyGeocodingAutoConfiguration.class
})
public class GeocodingProxyServer {

    private static final Logger LOG = LoggerFactory.getLogger(GeocodingProxyServer.class);

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(GeocodingProxyServer.class);
        app.setBannerMode(Banner.Mode.OFF);

        final ConfigurableApplicationContext ctx = app.run(args);
        ctx.registerShutdownHook();
        LOG.info("Started {}", GeocodingProxyServer.class.getSimpleName());
    }

}

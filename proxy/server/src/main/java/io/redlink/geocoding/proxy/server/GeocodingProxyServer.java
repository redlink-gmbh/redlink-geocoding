/*
 * Copyright (c) 2021 Redlink GmbH.
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

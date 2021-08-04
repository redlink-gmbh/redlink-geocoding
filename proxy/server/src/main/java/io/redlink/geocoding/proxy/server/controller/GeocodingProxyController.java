/*
 * Copyright (c) 2021 Redlink GmbH.
 */
package io.redlink.geocoding.proxy.server.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.redlink.geocoding.Geocoder;
import io.redlink.geocoding.LatLon;
import io.redlink.geocoding.proxy.io.Endpoints;
import io.redlink.geocoding.proxy.io.PlaceDTO;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = Endpoints.API_VERSION, produces = MediaType.APPLICATION_JSON_VALUE)
public class GeocodingProxyController {

    private static final Logger LOG = LoggerFactory.getLogger(GeocodingProxyController.class);

    private final Geocoder geocoder;
    private final MeterRegistry meterRegistry;

    public GeocodingProxyController(Geocoder geocoder, MeterRegistry meterRegistry) {
        this.geocoder = geocoder;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping
    public ResponseEntity<Void> ping() {
        meterRegistry.counter("ping").increment();
        return ResponseEntity.noContent().build();
    }

    @GetMapping(Endpoints.GEOCODE)
    public ResponseEntity<List<PlaceDTO>> geocode(
            @RequestParam(Endpoints.PARAM_ADDRESS) String address,
            @RequestParam(value = Endpoints.PARAM_LANG, required = false) Locale lang
    ) {
        try {
            LOG.debug("Geocode {} ({})", address, lang);
            meterRegistry.counter("geocode", createLangTag(lang)).increment();
            return ResponseEntity.ok(
                    geocoder.geocode(address, lang)
                            .stream()
                            .map(PlaceDTO::fromPlace)
                            .collect(Collectors.toList())
            );
        } catch (IOException e) {
            LOG.warn("Could not geocode {}: {}", address, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .build();
        }
    }

    @GetMapping(Endpoints.REVERSE_GEOCODE)
    public ResponseEntity<List<PlaceDTO>> reverseGeocode(
            @RequestParam(Endpoints.PARAM_LAT) double lat,
            @RequestParam(Endpoints.PARAM_LON) double lon,
            @RequestParam(value = Endpoints.PARAM_LANG, required = false) Locale lang
    ) {
        final LatLon coordinates = LatLon.create(lat, lon);
        try {
            LOG.debug("Reverse Geocode {},{} ({})", coordinates.lat(), coordinates.lon(), lang);
            meterRegistry.counter("geo-reverse", createLangTag(lang)).increment();
            return ResponseEntity.ok(
                    geocoder.reverseGeocode(coordinates, lang)
                            .stream()
                            .map(PlaceDTO::fromPlace)
                            .collect(Collectors.toList())
            );
        } catch (IOException e) {
            LOG.warn("Could not reverse geocode {}: {}", coordinates, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .build();
        }
    }

    @GetMapping(Endpoints.LOOKUP)
    public ResponseEntity<PlaceDTO> lookup(
            @RequestParam(Endpoints.PARAM_PLACE_ID) String placeId,
            @RequestParam(value = Endpoints.PARAM_LANG, required = false) Locale lang
    ) {
        try {
            LOG.debug("Lookup {} ({})", placeId, lang);
            meterRegistry.counter("lookup", createLangTag(lang)).increment();
            return ResponseEntity.of(
                    geocoder.lookup(placeId, lang)
                            .map(PlaceDTO::fromPlace)
            );
        } catch (IOException e) {
            LOG.warn("Could not lookup {}: {}", placeId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .build();
        }
    }

    private static Tags createLangTag(Locale lang) {
        return Tags.of("lang",
                Optional.ofNullable(lang)
                        .map(Locale::getLanguage)
                        .filter(StringUtils::isNotBlank)
                        .orElse("none")
        );
    }

}
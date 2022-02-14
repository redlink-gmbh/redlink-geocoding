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
import java.util.regex.Pattern;
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

    private static final Pattern SANITIZE_PATTERN = Pattern.compile("[\t\r\n]+");

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
            if (LOG.isDebugEnabled()) {
                LOG.debug("Geocode {} ({})", sanitizeForLog(address), lang);
            }

            meterRegistry.counter("geocode", createLangTag(lang)).increment();
            return ResponseEntity.ok(
                    geocoder.geocode(address, lang)
                            .stream()
                            .map(PlaceDTO::fromPlace)
                            .collect(Collectors.toList())
            );
        } catch (IOException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Could not geocode {}: {}", sanitizeForLog(address), e.getMessage(), e);
            }
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
            if (LOG.isDebugEnabled()) {
                LOG.debug("Lookup {} ({})", sanitizeForLog(placeId), lang);
            }
            meterRegistry.counter("lookup", createLangTag(lang)).increment();
            return ResponseEntity.of(
                    geocoder.lookup(placeId, lang)
                            .map(PlaceDTO::fromPlace)
            );
        } catch (IOException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Could not lookup {}: {}", sanitizeForLog(placeId), e.getMessage(), e);
            }
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

    private static String sanitizeForLog(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        return SANITIZE_PATTERN.matcher(input).replaceAll(" ");
    }

}

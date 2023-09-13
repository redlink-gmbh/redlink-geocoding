# Redlink Geocoding Library

[![Build, Test & Publish](https://github.com/redlink-gmbh/redlink-geocoding/actions/workflows/maven-build-and-deploy.yaml/badge.svg)](https://github.com/redlink-gmbh/redlink-geocoding/actions/workflows/maven-build-and-deploy.yaml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=redlink-gmbh_redlink-geocoding&metric=alert_status)](https://sonarcloud.io/dashboard?id=redlink-gmbh_redlink-geocoding)

[![Maven Central](https://img.shields.io/maven-central/v/io.redlink.geocoding/geocoding.png)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.redlink.geocoding%22)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/io.redlink.geocoding/geocoding.png)](https://oss.sonatype.org/#nexus-search;gav~io.redlink.geocoding~~~~)
[![Javadocs](https://www.javadoc.io/badge/io.redlink.geocoding/geocoding.svg)](https://www.javadoc.io/doc/io.redlink.geocoding/geocoding)
[![Apache 2.0 License](https://img.shields.io/github/license/redlink-gmbh/redlink-geocoding.svg)](https://www.apache.org/licenses/LICENSE-2.0)

The Redlink geocoding library provides the means to perform geographical 
information enhancement based on different services. At the current 
development state it is possible to do based on [Google Maps](https://developers.google.com/maps/documentation/geocoding/intro) 
or [Nominatim](https://wiki.openstreetmap.org/wiki/Nominatim) services:
 
* _geocoding_: given a partial address find places which could fit.
* _reverse geocoding_: providing coordinates find places located at that point.
* _lookup_: find a place given an ID (specific for the service used).

The library is divided in 5 main artifacts:

* API which contains the basic interface and generic classes to build up the real functionality.
* Google Maps Geocoder an implementation of the geocoder using the [Google Maps](https://developers.google.com/maps/documentation/geocoding/intro) service.
* Open Street Maps Geocoder an implementation of the geocoder using the [Nominatim](https://wiki.openstreetmap.org/wiki/Nominatim) service.
* Cache Geocoder is a wrapper for any Geocoder which uses basic guava cache to reduce the amount of calls made to the services and improve the response time.
* Proxy Geocoder provides a central geocoding proxy that forwards requests to one of the above providers.

## General (API)
This artifact contains the basic interface and classes to use the library or develop implementations for other geographical services. All the other artifacts on the library depend on this one.

The api artifact provides the basic `Geocoder` interface which defines usage of the three operations mentioned before.

```java
final Geocoder geocoder = ...

final List<Place> geocodedPlaces = geocoder.geocode("Jakob Haringer Strasse 3");
final List<Place> reverseGeocodedPlaces = geocoder.reverseGeocode(new LatLon(43.735762, 12.3029561));
final Place lookupPlace = geocoder.lookup("placeID");
```

**Note:** the `placeId` is implementation/backend specific, which means you can't use a `placeId` retrieved from the 
OSM based implementation and use it to lookup the place with the GoogleMaps based implementation. 

It also provides a basic representation of a geographical `Place` coordinate pair.

```java
final LatLon coordinates = new LatLon(47.8229144,13.0404834);
final Place geographicalPlace = new Place("placeID");
geographicalPlace.setAddress("Jakob Haringer Strasse 3");
geographicalPlace.setLatLon(coordinates);
```

Maven dependency:

```xml
<dependency>
    <groupId>io.redlink.geocoding</groupId>
    <artifactId>geocoding-api</artifactId>
    <version>${geocoding.version}</version>
</dependency>
```

## Google Maps Geocoder
This module implements of the geocoder artifact wrapping Google Maps Services java [client](https://github.com/googlemaps/google-maps-services-java).
To be able to use the Google Maps Services a valid API key should be provided to the library.

The `GoogleMapsGeocoder` object can be instantiate just by the means of the `GoogleMapsBuilder` which allows to configure
the specific configuration needed to use the Google Maps Services.


```java
final Geocoder googleGeocoder = GoogleMapsGeocoder.builder()
        .setApiKey("Googel_API_key")
        .setLocale("de")
        .create();
```

Maven dependency:

```xml
<dependency>
    <groupId>io.redlink.geocoding</groupId>
    <artifactId>geocoding-google</artifactId>
    <version>${geocoding.version}</version>
</dependency>
```

## Open Street Maps Geocoder
[Nominatim](http://wiki.openstreetmap.org/wiki/Nominatim) services Geocoder implementation, which provides the means to perform the three described operations using the aforementioned service.

```java
final Geocoder nominatimGeocoder = NominatimGeocoder.builder()
               .setEmail("example@email.org")
               .setLocale("en")
               .create();
```

Maven dependency:
```xml
<dependency>
    <groupId>io.redlink.geocoding</groupId>
    <artifactId>geocoding-osm</artifactId>
    <version>${geocoding.version}</version>
</dependency>
```

## Cache Geocoder
The geocoding-cache artifact implements a `CacheGeocoder` which actually wraps any other Geocoder object and provides a basic cache for the three supported methods to avoid  unnecessary replicated calls to the services and a shorter time response.

```java
final Geocoder cachingGeocoder = CacheGeocoder.builder()
               .setGeocoder(geocoder)
               .setCacheExpiry(24, TimeUnit.DAYS)
               .create();
```

Maven dependency:
```xml
<dependency>
    <groupId>io.redlink.geocoding</groupId>
    <artifactId>geocoding-cache</artifactId>
    <version>${geocoding.version}</version>
</dependency>
```

## Proxy Geocoder
The proxy-geocoder consists of two parts: a proxy-server and a client-side implementation to be used
instead (or in addition to) the above listed modules.

### Proxy Server
The Geocoding Proxy is provided as a docker-image and can be launched simply by running
```shell
docker run -d -p 8080:8080 -e NOMINATIM_BASE_URL=https://example.com ghcr.io/redlink-gmbh/redlink-geocoding/geocoding-proxy-server
```
The geocoding providers are autoconfigured using env-variables:
* For Google provide an API-key using `GOOGLE_API_KEY`
* For Nominatim, optionally provide `NOMINATIM_BASE_URL` and `NOMINATIM_MAIL`
* For Caching configure the cache-ttl: `GEO_CACHE_SECONDS`

### Proxy Client

The geocoding-proxy just needs to be configured with the base-url of the proxy-server:

```java
final Geocoder proxyGeocoder = ProxyGeocoder.builder()
               .setBaseUri("http://localhost:8080/")
               .create();
```

Maven dependency:
```xml
<dependency>
    <groupId>io.redlink.geocoding</groupId>
    <artifactId>geocoding-proxy</artifactId>
    <version>${geocoding.version}</version>
</dependency>
```

## Spring-Boot Autoconfiguration

For quick and easy use of the `Geocoder` in [spring-boot] projects use the `geocoding-spring-boot-autoconfigure` module,
and configure the geocoders with the following `application.properties`:

```properties
# GoogleMaps Geocoder, provide either api-key or client-id and crypto-secret
geocoding.google.api-key=
geocoding.google.client-id=
geocoding.google.crypto-secret=
geocoding.google.channel=
# Nominatim Geocoder
geocoding.nominatim.base-url=
geocoding.nominatim.email=
# Proxy Geocoder
geocoding.proxy-service.base-url=
# General Options
geocoding.cache-timeout=
geocoding.lang=
geocoding.proxy=
```

Maven dependency:
```xml
<dependencies>
    <dependency>
        <groupId>io.redlink.geocoding</groupId>
        <artifactId>geocoding-spring-boot-autoconfigure</artifactId>
        <version>${geocoding.version}</version>
    </dependency>
    <!-- Add an implementation provider, at least one: -->
    <dependency>
        <groupId>io.redlink.geocoding</groupId>
        <artifactId>geocoding-google</artifactId>
        <version>${geocoding.version}</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.redlink.geocoding</groupId>
        <artifactId>geocoding-osm</artifactId>
        <version>${geocoding.version}</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.redlink.geocoding</groupId>
        <artifactId>geocoding-proxy</artifactId>
        <version>${geocoding.version}</version>
        <scope>runtime</scope>
    </dependency>
    <!-- optionally the chache -->
    <dependency>
        <groupId>io.redlink.geocoding</groupId>
        <artifactId>geocoding-cache</artifactId>
        <version>${geocoding.version}</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```
## License
Free use of this software is granted under the terms of the Apache License Version 2.0.
See the [License](LICENSE.txt) for more details.


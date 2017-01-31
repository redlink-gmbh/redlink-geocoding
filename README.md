# Redlink Geocoding Library
The Redlink geocoding library provides the means to perform geographical 
information enhancement based on different services. At the current 
development state it is possible to do based on [Google Maps](https://developers.google.com/maps/documentation/geocoding/intro) 
or [Nominatim](http://wiki.openstreetmap.org/wiki/Nominatim) services:  
 * geocoding: given a partial address find places which could fit.  
 * reverse geocoding: providing coordinates find places located at that point.  
 * lookup: find a place given an ID (specific for the service used).  
The library is divided in 4 separate artifacts:
 * API which contains the basic interface and generic classes to build up the real functionality.  
 * Google Maps Geocoder an implementation of the geocoder using the [Google Maps](https://developers.google.com/maps/documentation/geocoding/intro) service.
 * Open Street Maps Geocoder an implementation of the geocoder using the [Nominatim](http://wiki.openstreetmap.org/wiki/Nominatim) service.
 * Cache Geocoder is a wrapper for any Geocoder which uses basic guava cache to reduce the amount of calls made to the services and improve the response time.

## API
'''xml
<dependency>
    <groupId>io.redlink.geocoding</groupId>
    <artifactId>geocoding-api</artifactId>
    <version>${project.version}</version>
</dependency>
'''
## Google Maps Geocoder
'''xml
<dependency>
    <groupId>io.redlink.geocoding</groupId>
    <artifactId>geocoding-google</artifactId>
    <version>${project.version}</version>
</dependency>
'''
## Open Street Maps Geocoder
'''xml
<dependency>
    <groupId>io.redlink.geocoding</groupId>
    <artifactId>geocoding-osm</artifactId>
    <version>${project.version}</version>
</dependency>
'''
## Cache Geocoder
'''xml
<dependency>
    <groupId>io.redlink.geocoding</groupId>
    <artifactId>geocoding-cache</artifactId>
    <version>${project.version}</version>
</dependency>
'''

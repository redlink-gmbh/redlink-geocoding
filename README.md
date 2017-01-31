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
This artifact contains the basic interface and classes to use the library or develop implementations for other geographical services. All the other artifacts on the library depend on this one.

The API artifact provides the basic [Geocoder](https://bitbucket.org/redlinkgmbh/geocoding/src/5d38badc7e578acce6dbd05950c20b95f9358f19/api/src/main/java/io/redlink/geocoding/Geocoder.java?at=master&fileviewer=file-view-default) interface which defines usage of the three operations mentioned before.
```java
final Geocoder geocoder = new GeocoderImplementation();

final List<Place> geocodedPlaces = geocoder.geocode("Jakob Haringer Strasse 3");
final List<Place> reverseGeocodedPlaces = geocoder.reverseGeocode(new LatLon(43.735762, 12.3029561));
final Place lookupPlace = geocoder.lookup("placeID");
```
It also provides a basic representation of a geographical [Place](https://bitbucket.org/redlinkgmbh/geocoding/src/5d38badc7e578acce6dbd05950c20b95f9358f19/api/src/main/java/io/redlink/geocoding/Place.java?at=master&fileviewer=file-view-default) used as result of the different allowed operations and a representation of a [LatLon](https://bitbucket.org/redlinkgmbh/geocoding/src/5d38badc7e578acce6dbd05950c20b95f9358f19/api/src/main/java/io/redlink/geocoding/LatLon.java?at=master&fileviewer=file-view-default) coordinate pair.

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
    <version>${project.version}</version>
</dependency>
```
## Google Maps Geocoder
```xml
<dependency>
    <groupId>io.redlink.geocoding</groupId>
    <artifactId>geocoding-google</artifactId>
    <version>${project.version}</version>
</dependency>
```
## Open Street Maps Geocoder
```xml
<dependency>
    <groupId>io.redlink.geocoding</groupId>
    <artifactId>geocoding-osm</artifactId>
    <version>${project.version}</version>
</dependency>
```
## Cache Geocoder
```xml
<dependency>
    <groupId>io.redlink.geocoding</groupId>
    <artifactId>geocoding-cache</artifactId>
    <version>${project.version}</version>
</dependency>
```
module io.redlink.geocoding.osm {
    requires transitive io.redlink.geocoding.api;
    requires org.slf4j;
    requires org.apache.commons.lang3;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.jsoup;
    requires com.google.common;

    exports io.redlink.geocoding.nominatim;
}

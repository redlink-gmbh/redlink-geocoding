module io.redlink.geocoding.osm {
    requires transitive io.redlink.geocoding.api;
    requires org.slf4j;
    requires org.apache.commons.lang3;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.jsoup;
    requires com.google.common;

    exports io.redlink.geocoding.nominatim;
}

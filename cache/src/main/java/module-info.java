module io.redlink.geocoding.cache {
    requires transitive io.redlink.geocoding.api;
    requires org.slf4j;

    requires com.google.common;

    exports io.redlink.geocoding.cache;
}

module io.redlink.geocoding.proxy {
    requires transitive io.redlink.geocoding.api;
    requires org.slf4j;

    requires com.fasterxml.jackson.databind;
    requires io.redlink.geocoding.proxy.commons;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.apache.httpcomponents.client5.httpclient5;

    exports io.redlink.geocoding.proxy;
}

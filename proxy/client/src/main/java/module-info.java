module io.redlink.geocoding.proxy {
    requires transitive io.redlink.geocoding.api;

    requires com.fasterxml.jackson.databind;
    requires io.redlink.geocoding.proxy.commons;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;

    exports io.redlink.geocoding.proxy;
}

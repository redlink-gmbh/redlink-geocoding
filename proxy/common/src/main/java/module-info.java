module io.redlink.geocoding.proxy.commons {
    requires transitive io.redlink.geocoding.api;
    requires com.fasterxml.jackson.annotation;

    exports io.redlink.geocoding.proxy.io to
            io.redlink.geocoding.proxy,
            io.redlink.geocoding.proxy.server;
    opens io.redlink.geocoding.proxy.io to
            com.fasterxml.jackson.databind;
}

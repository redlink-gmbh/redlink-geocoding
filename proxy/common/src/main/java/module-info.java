module io.redlink.geocoding.proxy.commons {
    requires transitive io.redlink.geocoding.api;
    requires com.fasterxml.jackson.annotation;

    exports io.redlink.geocoding.proxy.io to
            io.redlink.geocoding.proxy;
    opens io.redlink.geocoding.proxy.io to
            com.fasterxml.jackson.databind;
}

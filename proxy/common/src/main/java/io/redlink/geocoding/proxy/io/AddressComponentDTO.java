/*
 * Copyright (c) 2021 Redlink GmbH.
 */
package io.redlink.geocoding.proxy.io;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.redlink.geocoding.AddressComponent;

public final class AddressComponentDTO {

    private final AddressComponent.Type type;
    private final String value;

    private AddressComponentDTO(AddressComponent.Type type, String value) {
        this.type = type;
        this.value = value;
    }

    @JsonProperty("type")
    public AddressComponent.Type getType() {
        return type;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    public AddressComponent toAddressComponent() {
        return AddressComponent.create(type, value);
    }

    public static AddressComponentDTO fromAddressComponent(AddressComponent ac) {
        return create(ac.getType(), ac.getValue());
    }

    @JsonCreator
    public static AddressComponentDTO create(
            @JsonProperty("type") AddressComponent.Type type,
            @JsonProperty("value") String value
    ) {
        return new AddressComponentDTO(type, value);
    }

}

/*
 * Copyright (c) 2022 Redlink GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.redlink.geocoding;

public final class AddressComponent implements Comparable<AddressComponent> {

    private final Type type;
    private final String value;

    private AddressComponent(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(AddressComponent o) {
        return Integer.compare(type.ordinal(), o.type.ordinal());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AddressComponent other = (AddressComponent) obj;
        if (type != other.type) return false;
        if (value == null) {
            if (other.value != null) return false;
        } else if (!value.equals(other.value)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "AddressComponent [type=" + type + ", value=" + value + "]";
    }

    public static AddressComponent create(Type type, String value) {
        return new AddressComponent(type, value);
    }

    @SuppressWarnings("java:S115")
    public enum Type {
        streetNumber,
        street,
        sublocality,
        postalCode,
        city,
        state,
        countryCode,
        country,
    }

}

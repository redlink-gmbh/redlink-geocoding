package io.redlink.geocoding;

public class AddressComponent implements Comparable<AddressComponent>{

    private final Type type;
    private final String value;
    
    public AddressComponent(Type type, String value) {
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
        return Integer.compare(type.ordinal(),o.type.ordinal());
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AddressComponent other = (AddressComponent) obj;
        if (type != other.type)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AddressComponent [type=" + type + ", value=" + value + "]";
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

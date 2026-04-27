package org.fabricate.model;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

public record Address(
        String street,
        String city,
        String state,
        String postalCode,
        String country
) {
    public Address {
        Objects.requireNonNull(street, "street");
        Objects.requireNonNull(city, "city");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(postalCode, "postalCode");
        Objects.requireNonNull(country, "country");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @Nullable String street;
        private @Nullable String city;
        private @Nullable String state;
        private @Nullable String postalCode;
        private @Nullable String country;

        private Builder() {}

        public Builder street(String street) { this.street = street; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }
        public Builder country(String country) { this.country = country; return this; }

        public Address build() {
            return new Address(
                    Objects.requireNonNull(street, "street"),
                    Objects.requireNonNull(city, "city"),
                    Objects.requireNonNull(state, "state"),
                    Objects.requireNonNull(postalCode, "postalCode"),
                    Objects.requireNonNull(country, "country")
            );
        }
    }
}

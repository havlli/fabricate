package org.fabricate.model;

import java.time.Instant;
import java.util.Objects;
import org.fabricate.store.LocaleConstraint;
import org.jspecify.annotations.Nullable;

public record Person(
        String firstName,
        String lastName,
        String email,
        @Nullable String uid,
        @Nullable String username,
        @Nullable String password,
        @Nullable String phoneNumber,
        @Nullable Address address,
        @Nullable Instant dateOfBirth,
        @Nullable String jobTitle,
        @Nullable LocaleConstraint localeConstraint
) {
    public Person {
        Objects.requireNonNull(firstName, "firstName");
        Objects.requireNonNull(lastName, "lastName");
        Objects.requireNonNull(email, "email");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @Nullable String firstName;
        private @Nullable String lastName;
        private @Nullable String email;
        private @Nullable String uid;
        private @Nullable String username;
        private @Nullable String password;
        private @Nullable String phoneNumber;
        private @Nullable Address address;
        private @Nullable Instant dateOfBirth;
        private @Nullable String jobTitle;
        private @Nullable LocaleConstraint localeConstraint;

        private Builder() {}

        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder uid(@Nullable String uid) { this.uid = uid; return this; }
        public Builder username(@Nullable String username) { this.username = username; return this; }
        public Builder password(@Nullable String password) { this.password = password; return this; }
        public Builder phoneNumber(@Nullable String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder address(@Nullable Address address) { this.address = address; return this; }
        public Builder dateOfBirth(@Nullable Instant dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public Builder jobTitle(@Nullable String jobTitle) { this.jobTitle = jobTitle; return this; }
        public Builder localeConstraint(@Nullable LocaleConstraint localeConstraint) {
            this.localeConstraint = localeConstraint;
            return this;
        }

        public Person build() {
            return new Person(
                    Objects.requireNonNull(firstName, "firstName"),
                    Objects.requireNonNull(lastName, "lastName"),
                    Objects.requireNonNull(email, "email"),
                    uid, username, password, phoneNumber, address,
                    dateOfBirth, jobTitle, localeConstraint
            );
        }
    }
}

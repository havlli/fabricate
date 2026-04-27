package org.fabricate.model;

import java.time.LocalDate;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public record Person(
        String firstName,
        String lastName,
        String email,
        @Nullable String uid,
        @Nullable String username,
        @Nullable String phoneNumber,
        @Nullable Address address,
        @Nullable LocalDate dateOfBirth,
        @Nullable String jobTitle
) {
    public Person {
        Objects.requireNonNull(firstName, "firstName");
        Objects.requireNonNull(lastName, "lastName");
        Objects.requireNonNull(email, "email");
    }
}

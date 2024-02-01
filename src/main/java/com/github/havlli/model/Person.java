package com.github.havlli.model;

import com.github.havlli.store.LocaleConstraint;
import lombok.Builder;
import lombok.NonNull;

import java.time.Instant;

@Builder
public record Person(
        @NonNull
        String firstName,
        @NonNull
        String lastName,
        @NonNull
        String email,
        String uid,

        String username,

        String password,

        String phoneNumber,

        Address address,

        Instant dateOfBirth,

        String jobTitle,

        LocaleConstraint localeConstraint
) { }

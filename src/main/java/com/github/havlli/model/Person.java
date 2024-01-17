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
        @NonNull
        String username,
        @NonNull
        String password,
        @NonNull
        String phoneNumber,
        @NonNull
        Address address,
        @NonNull
        Instant dateOfBirth,
        @NonNull
        String jobTitle,
        @NonNull
        LocaleConstraint localeConstraint
) { }

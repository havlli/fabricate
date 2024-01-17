package com.github.havlli.model;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record Address(
        @NonNull
        String street,
        @NonNull
        String city,
        @NonNull
        String state,
        @NonNull
        String postalCode,
        @NonNull
        String country
) { }

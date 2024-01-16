package com.github.havlli.model;

import lombok.Builder;

@Builder
public record Address(
        String street,
        String city,
        String state,
        String postalCode,
        String country
) { }

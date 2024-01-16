package com.github.havlli.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record Person(
        String firstName,
        String lastName,
        String email,
        String username,
        String password,
        String phoneNumber,
        Address address,
        Instant dateOfBirth,
        String jobTitle
) { }

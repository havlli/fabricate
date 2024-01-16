package com.github.havlli.model;

import java.time.Instant;

public class Person {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String username;
    private final String password;
    private final String phoneNumber;
    private final Address address;
    private final Instant dateOfBirth;
    private final String jobTitle;

    private Person(String firstName, String lastName, String email, String username, String password, String phoneNumber, Address address, Instant dateOfBirth, String jobTitle) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.jobTitle = jobTitle;
    }

}

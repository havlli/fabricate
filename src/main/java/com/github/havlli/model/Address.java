package com.github.havlli.model;

public class Address {
    private final String street;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;

    public Address(String street, String city, String state, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }
}

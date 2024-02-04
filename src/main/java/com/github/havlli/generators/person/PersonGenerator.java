package com.github.havlli.generators.person;

import com.github.havlli.model.Address;
import com.github.havlli.model.Person;

import java.time.Instant;

public interface PersonGenerator {
    String generateFirstName();
    String generateLastName();
    String generateUID();
    String generateEmail(String firstName, String lastName);
    String generatePassword();
    String generateUsername(String firstName, String lastName, String uid);
    String generatePhoneNumber();
    Address generateAddress();
    Instant generateDateOfBirth();
    String generateJobTittle();
    Person generate();
}

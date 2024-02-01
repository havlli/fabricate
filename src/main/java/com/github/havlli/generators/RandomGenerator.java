package com.github.havlli.generators;

import com.github.havlli.generators.person.RandomPersonGenerator;
import com.github.havlli.model.Person;
import com.github.havlli.store.LocaleStore;

public class RandomGenerator implements Generator {

    private final RandomPersonGenerator personGenerator;

    public RandomGenerator(LocaleStore localeStore) {
        this.personGenerator = new RandomPersonGenerator(localeStore);
    }

    @Override
    public Person generatePerson() {
        String firstName = personGenerator.generateFirstName();
        String lastName = personGenerator.generateLastName();
        String uid = personGenerator.generateUID();

        return Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(personGenerator.generateEmail(firstName, lastName))
                .uid(uid)
                .username(personGenerator.generateUsername(firstName, lastName, uid))
                .password(personGenerator.generatePassword())
                .phoneNumber(personGenerator.generatePhoneNumber())
                .address(personGenerator.generateAddress())
                .dateOfBirth(personGenerator.generateDateOfBirth())
                .jobTitle(personGenerator.generateJobTittle())
                .build();
    }
}

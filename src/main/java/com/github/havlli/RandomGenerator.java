package com.github.havlli;

import com.github.havlli.model.Person;
import com.github.havlli.store.LocaleStore;

import java.util.Random;

public class RandomGenerator implements Generator {
    private final LocaleStore localeStore;
    private final String[] firstNames;
    private final String[] lastNames;
    private final String[] emailDomains;
    private final Random random;


    public RandomGenerator(LocaleStore localeStore) {
        this.localeStore = localeStore;
        this.firstNames = localeStore.getFirstNames();
        this.lastNames = localeStore.getLastNames();
        this.emailDomains = localeStore.getEmailDomains();
        this.random = new Random();
    }

    public String generateEmail() {
        String firstName = getRandomValueFrom(firstNames);
        String lastName = getRandomValueFrom(lastNames);
        return firstName + localeStore.getNameDelimiter() + lastName;
    }

    private String getRandomValueFrom(String[] values) {
        return values[random.nextInt(values.length)];
    }

    @Override
    public Person generatePerson() {
        String firstName = getRandomValueFrom(firstNames);
        String lastName = getRandomValueFrom(lastNames);
        String email = constructEmail(firstName + "." + lastName);

        return Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
    }

    private String constructEmail(String value) {
        return localeStore.getEmailLocalPart(value) + "@" + emailDomains[random.nextInt(emailDomains.length)];
    }
}

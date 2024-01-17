package com.github.havlli;

import com.github.havlli.model.Person;
import com.github.havlli.store.LocaleStore;

import java.util.Random;
import java.util.UUID;

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

    private String getRandomValueFrom(String[] values) {
        return values[random.nextInt(values.length)];
    }

    @Override
    public Person generatePerson() {
        String firstName = getRandomValueFrom(firstNames);
        String lastName = getRandomValueFrom(lastNames);
        String email = generateEmail(firstName + "." + lastName);
        UUID uuid = UUID.randomUUID();
        String username = generateUsername(firstName, lastName, uuid);

        return Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .uuid(uuid)
                .username(username)
                .build();
    }

    private String generateEmail(String value) {
        return localeStore.getEmailLocalPart(value.toLowerCase()) + "@" + emailDomains[random.nextInt(emailDomains.length)];
    }

    public String generateUsername(String firstName, String lastName, UUID uuid) {
        String uuidPart = uuid.toString().split("-")[0]; // Take first part of UUID
        return firstName.toLowerCase() + lastName.toLowerCase().charAt(0) + uuidPart;
    }
}

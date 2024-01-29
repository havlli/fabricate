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
    private final String[] regionPhoneNumbers;
    private final Random random;


    public RandomGenerator(LocaleStore localeStore) {
        this.localeStore = localeStore;
        this.firstNames = localeStore.getFirstNames();
        this.lastNames = localeStore.getLastNames();
        this.emailDomains = localeStore.getEmailDomains();
        this.regionPhoneNumbers = localeStore.getRegionPhoneNumbers();
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
        String password = generatePassword();
        String phoneNumber = generatePhoneNumber();

        return Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .uuid(uuid)
                .username(username)
                .password(password)
                .phoneNumber(phoneNumber)
                .build();
    }

    private String generatePassword() {
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
        Random random = new Random();
        int length = 8;

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charSet.length());
            sb.append(charSet.charAt(randomIndex));
        }

        return sb.toString();
    }

    private String generateEmail(String value) {
        return localeStore.getEmailLocalPart(value.toLowerCase()) + "@" + emailDomains[random.nextInt(emailDomains.length)];
    }

    public String generateUsername(String firstName, String lastName, UUID uuid) {
        String uuidPart = uuid.toString().split("-")[0]; // Take first part of UUID
        return firstName.toLowerCase() + lastName.toLowerCase().charAt(0) + uuidPart;
    }

    public String generatePhoneNumber() {
        String regionPrefix = getRandomValueFrom(regionPhoneNumbers);

        StringBuilder stringBuilder = new StringBuilder(regionPrefix);

        for (int i=0; i < 9; i++) {
            stringBuilder.append(random.nextInt(9));
        }

        return stringBuilder.toString();
    }
}

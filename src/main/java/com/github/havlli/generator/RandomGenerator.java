package com.github.havlli.generator;

import com.github.havlli.model.Address;
import com.github.havlli.model.Person;
import com.github.havlli.store.LocaleStore;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

public class RandomGenerator implements Generator {
    private final LocaleStore localeStore;
    private final Random random;


    public RandomGenerator(LocaleStore localeStore) {
        this.localeStore = localeStore;
        this.random = new Random();
    }

    private String getRandomValueFrom(String[] values) {
        return values[random.nextInt(values.length)];
    }

    @Override
    public Person generatePerson() {
        String firstName = getRandomValueFrom(localeStore.getFirstNames());
        String lastName = getRandomValueFrom(localeStore.getLastNames());
        UUID uuid = UUID.randomUUID();

        return Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(generateEmail(firstName, lastName))
                .uuid(uuid)
                .username(generateUsername(firstName, lastName, uuid))
                .password(generatePassword())
                .phoneNumber(generatePhoneNumber())
                .address(generateAddress())
                .dateOfBirth(generateDateOfBirth())
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

    private String generateEmail(String firstName, String lastName) {
        String[] emailDomains = localeStore.getEmailDomains();
        return localeStore.getEmailLocalPart(firstName + "." + lastName) + "@" + emailDomains[random.nextInt(emailDomains.length)];
    }

    public String generateUsername(String firstName, String lastName, UUID uuid) {
        String uuidPart = uuid.toString().split("-")[0]; // Take first part of UUID
        return firstName.toLowerCase() + lastName.toLowerCase().charAt(0) + uuidPart;
    }

    public String generatePhoneNumber() {
        String regionPrefix = getRandomValueFrom(localeStore.getRegionPhoneNumbers());

        StringBuilder stringBuilder = new StringBuilder(regionPrefix);

        for (int i=0; i < 9; i++) {
            stringBuilder.append(random.nextInt(9));
        }

        return stringBuilder.toString();
    }

    public Address generateAddress() {
        return Address.builder()
                .street(getRandomValueFrom(localeStore.getStreets()))
                .city(getRandomValueFrom(localeStore.getCities()))
                .state(getRandomValueFrom(localeStore.getStates()))
                .postalCode(getRandomValueFrom(localeStore.getPostalCodes()))
                .country(getRandomValueFrom(localeStore.getCountries()))
                .build();
    }

    public Instant generateDateOfBirth() {
        long minAgeInDays = 18 * 365;
        long maxAgeInDays = 90 * 365;

        long randomDays = minAgeInDays + random.nextLong(maxAgeInDays - minAgeInDays);

        return Instant.now()
                .minus(random.nextLong(365), ChronoUnit.DAYS)
                .minus(randomDays, ChronoUnit.DAYS);
    }
}

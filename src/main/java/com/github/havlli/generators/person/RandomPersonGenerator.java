package com.github.havlli.generators.person;

import com.github.havlli.generators.GeneratorService;
import com.github.havlli.generators.GeneratorServiceImpl;
import com.github.havlli.model.Address;
import com.github.havlli.store.LocaleStore;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

public class RandomPersonGenerator implements PersonGenerator {

    private final LocaleStore localeStore;
    private final GeneratorService generatorService;
    private final Random random;

    public RandomPersonGenerator(LocaleStore localeStore) {
        this.localeStore = localeStore;
        this.generatorService = new GeneratorServiceImpl();
        this.random = generatorService.createRandom();
    }

    @Override
    public String generateFirstName() {
        return getRandomValueFrom(localeStore.getFirstNames());
    }

    @Override
    public String generateLastName() {
        return getRandomValueFrom(localeStore.getLastNames());
    }

    @Override
    public String generateUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String generateEmail(String firstName, String lastName) {
        String[] emailDomains = localeStore.getEmailDomains();
        return localeStore.getEmailLocalPart(firstName + "." + lastName) + "@" + emailDomains[random.nextInt(emailDomains.length)];
    }

    @Override
    public String generatePassword() {
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
        int length = 8;

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charSet.length());
            sb.append(charSet.charAt(randomIndex));
        }

        return sb.toString();
    }

    @Override
    public String generateUsername(String firstName, String lastName, String uid) {
        String uidPart = uid.split("-")[0]; // Take first part of UID
        return firstName.toLowerCase() + lastName.toLowerCase().charAt(0) + uidPart;
    }

    @Override
    public String generatePhoneNumber() {
        String regionPrefix = getRandomValueFrom(localeStore.getRegionPhoneNumbers());

        StringBuilder stringBuilder = new StringBuilder(regionPrefix);

        for (int i=0; i < 9; i++) {
            stringBuilder.append(random.nextInt(9));
        }

        return stringBuilder.toString();
    }

    @Override
    public Address generateAddress() {
        return Address.builder()
                .street(getRandomValueFrom(localeStore.getStreets()))
                .city(getRandomValueFrom(localeStore.getCities()))
                .state(getRandomValueFrom(localeStore.getStates()))
                .postalCode(getRandomValueFrom(localeStore.getPostalCodes()))
                .country(getRandomValueFrom(localeStore.getCountries()))
                .build();
    }

    @Override
    public Instant generateDateOfBirth() {
        long minAgeInDays = 18 * 365;
        long maxAgeInDays = 90 * 365;

        long randomDays = minAgeInDays + random.nextLong(maxAgeInDays - minAgeInDays);

        return Instant.now()
                .minus(random.nextLong(365), ChronoUnit.DAYS)
                .minus(randomDays, ChronoUnit.DAYS);
    }

    @Override
    public String generateJobTittle() {
        return getRandomValueFrom(localeStore.getJobTitles());
    }

    private String getRandomValueFrom(String[] values) {
        return generatorService.getRandomValueFrom(values);
    }
}

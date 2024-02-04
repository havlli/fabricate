package com.github.havlli.generators.person;

import com.github.havlli.generators.address.AddressGenerator;
import com.github.havlli.generators.address.RandomAddressGenerator;
import com.github.havlli.generators.strategies.GeneratorStrategy;
import com.github.havlli.generators.strategies.SimpleRandomGeneratorStrategy;
import com.github.havlli.model.Address;
import com.github.havlli.model.Person;
import com.github.havlli.store.LocaleStore;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

public class RandomPersonGenerator implements PersonGenerator {

    private final LocaleStore localeStore;
    private final GeneratorStrategy generatorStrategy;
    private final AddressGenerator addressGenerator;
    private final Random random;

    public RandomPersonGenerator(LocaleStore localeStore) {
        this.localeStore = localeStore;
        this.generatorStrategy = new SimpleRandomGeneratorStrategy();
        this.addressGenerator = new RandomAddressGenerator(localeStore, generatorStrategy);
        this.random = new Random(System.nanoTime());
    }

    public RandomPersonGenerator(LocaleStore localeStore, GeneratorStrategy generatorStrategy) {
        this.localeStore = localeStore;
        this.generatorStrategy = generatorStrategy;
        this.addressGenerator = new RandomAddressGenerator(localeStore, generatorStrategy);
        this.random = new Random(System.nanoTime());
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
        return addressGenerator.generate();
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

    @Override
    public Person generate() {
        String firstName = generateFirstName();
        String lastName = generateLastName();
        String uid = generateUID();

        return Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(generateEmail(firstName, lastName))
                .uid(uid)
                .username(generateUsername(firstName, lastName, uid))
                .password(generatePassword())
                .phoneNumber(generatePhoneNumber())
                .address(generateAddress())
                .dateOfBirth(generateDateOfBirth())
                .jobTitle(generateJobTittle())
                .build();
    }

    private String getRandomValueFrom(String[] values) {
        return generatorStrategy.generationStrategy()
                .apply(values);
    }
}

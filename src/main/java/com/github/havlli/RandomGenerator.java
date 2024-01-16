package com.github.havlli;

import com.github.havlli.store.LocaleStore;

import java.util.Random;

public class RandomGenerator {
    private final LocaleStore localeStore;
    private final String[] firstNames;
    private final String[] lastNames;
    private final Random random;


    public RandomGenerator(LocaleStore localeStore) {
        this.localeStore = localeStore;
        this.firstNames = localeStore.getFirstNames();
        this.lastNames = localeStore.getLastNames();
        this.random = new Random();
    }

    public String generate() {
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(firstNames.length)];
        return firstName + localeStore.getNameDelimiter() + lastName;
    }
}

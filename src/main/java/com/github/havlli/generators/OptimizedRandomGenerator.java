package com.github.havlli.generators;

import com.github.havlli.store.LocaleStore;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class OptimizedRandomGenerator {
    private final int bufferSize;
    private final Set<String> recentNames = new HashSet<>();
    private final LocaleStore localeStore;
    private final String[] firstNames;
    private final String[] lastNames;
    private final Random random;

    public OptimizedRandomGenerator(LocaleStore localeStore) {
        this.localeStore = localeStore;
        this.firstNames = localeStore.getFirstNames();
        this.lastNames = localeStore.getLastNames();
        this.random = new Random(System.nanoTime());
        this.bufferSize = 100;
    }

    public OptimizedRandomGenerator(LocaleStore localeStore, int bufferSize) {
        this.localeStore = localeStore;
        this.firstNames = localeStore.getFirstNames();
        this.lastNames = localeStore.getLastNames();
        this.random = new Random(System.nanoTime());
        this.bufferSize = bufferSize;
    }

    public String generate() {
        String fullName;
        do {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(firstNames.length)];

            fullName = firstName + localeStore.getNameDelimiter() + lastName;
        } while (recentNames.contains(fullName));

        recentNames.add(fullName);
        if (recentNames.size() > bufferSize) {
            recentNames.remove(recentNames.iterator().next());
        }

        return fullName;
    }
}

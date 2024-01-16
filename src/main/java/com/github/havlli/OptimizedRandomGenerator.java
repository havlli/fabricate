package com.github.havlli;

import com.github.havlli.store.LocaleStore;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class OptimizedRandomGenerator {
    private static final int MEMORY_BUFFER = 500;
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
    }

    public String generate() {
        String fullName;
        do {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(firstNames.length)];

            fullName = firstName + localeStore.getNameDelimiter() + lastName;
        } while (recentNames.contains(fullName));

        recentNames.add(fullName);
        if (recentNames.size() > MEMORY_BUFFER) {
            recentNames.remove(recentNames.iterator().next());
        }

        return fullName;
    }
}

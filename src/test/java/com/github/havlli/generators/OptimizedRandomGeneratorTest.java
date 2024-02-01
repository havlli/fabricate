package com.github.havlli.generators;

import com.github.havlli.store.LocaleStore;
import com.github.havlli.store.LocaleStoreFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

class OptimizedRandomGeneratorTest {

    @Test
    void generate_GeneratesNoDuplicates_WhenBufferEqualsSizeOfRequestedSample() {
        // Arrange
        int sampleSize = 500;
        int bufferSize = 500;

        LocaleStore localeStore = LocaleStoreFactory.getLocaleStore(Locale.ENGLISH);
        OptimizedRandomGenerator optimizedRandomGenerator = new OptimizedRandomGenerator(localeStore, bufferSize);

        // Act
        List<String> generatedPersons = IntStream.range(0, sampleSize)
                .mapToObj(i -> optimizedRandomGenerator.generate())
                .toList();

        // Assert
        generatedPersons.forEach(System.out::println);

        int size = generatedPersons.size();
        int sizeWithoutDuplicates = ((int) generatedPersons.stream().distinct().count());
        int numberOfDuplicates = size - sizeWithoutDuplicates;

        System.out.printf("For sample of size %d was generated %d duplicates!", size, numberOfDuplicates);
    }
}
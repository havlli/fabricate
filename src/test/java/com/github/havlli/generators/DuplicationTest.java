package com.github.havlli.generators;

import com.github.havlli.model.Person;
import com.github.havlli.store.LocaleStore;
import com.github.havlli.store.LocaleStoreFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DuplicationTest {

    @ParameterizedTest
    @ValueSource(ints = { 100, 200, 500, 1000, 1500, 2000 })
    void optimizedGenerator_generateNames_CountDuplicateNames_WhenBufferSizeIsStatic(int sampleSize) {
        // Arrange
        int bufferSize = 500;

        LocaleStore localeStore = LocaleStoreFactory.getLocaleStore(Locale.ENGLISH);
        OptimizedRandomGenerator optimizedRandomGenerator = new OptimizedRandomGenerator(localeStore, bufferSize);

        // Act
        long startTime = System.currentTimeMillis();
        List<String> generatedPersons = IntStream.range(0, sampleSize)
                .mapToObj(i -> optimizedRandomGenerator.generate())
                .toList();
        long deficitTime = System.currentTimeMillis() - startTime;

        // Assert
        int size = generatedPersons.size();
        int sizeWithoutDuplicates = ((int) generatedPersons.stream().distinct().count());
        int numberOfDuplicates = size - sizeWithoutDuplicates;

        System.out.printf("For sample of size %d was generated %d duplicates!%n", size, numberOfDuplicates);
        System.out.printf("Execution time: %dms", deficitTime);
    }

    @ParameterizedTest
    @ValueSource(ints = { 100, 200, 500, 1000, 1225 })
    void optimizedGenerator_generateNames_CountDuplicateNames_WhenBufferSizeEqualsToSampleSize(int sampleSize) {
        // Arrange
        LocaleStore localeStore = LocaleStoreFactory.getLocaleStore(Locale.ENGLISH);
        OptimizedRandomGenerator optimizedRandomGenerator = new OptimizedRandomGenerator(localeStore, sampleSize);

        // Act
        long startTime = System.currentTimeMillis();
        List<String> generatedPersons = IntStream.range(0, sampleSize)
                .mapToObj(i -> optimizedRandomGenerator.generate())
                .toList();
        long deficitTime = System.currentTimeMillis() - startTime;

        // Assert
        int size = generatedPersons.size();
        int sizeWithoutDuplicates = ((int) generatedPersons.stream().distinct().count());
        int numberOfDuplicates = size - sizeWithoutDuplicates;

        System.out.printf("For sample of size %d was generated %d duplicates!%n", size, numberOfDuplicates);
        System.out.printf("Execution time: %dms", deficitTime);
    }

    @ParameterizedTest
    @ValueSource(ints = { 1226, 1500, 2000, 2500 })
    void optimizedGenerator_Constructor_ThrowsException_WhenBufferSizeIsBiggerThenUniquePossibleCombination(int sampleSize) {
        // Arrange
        LocaleStore localeStore = LocaleStoreFactory.getLocaleStore(Locale.ENGLISH);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> new OptimizedRandomGenerator(localeStore, sampleSize));
    }

    @ParameterizedTest
    @ValueSource(ints = { 100, 200, 500, 1000, 1500, 2000 })
    void optimizedGenerator_generateNames_CountDuplicateNames_WhenBufferSizeNotSet(int sampleSize) {
        // Arrange

        LocaleStore localeStore = LocaleStoreFactory.getLocaleStore(Locale.ENGLISH);
        OptimizedRandomGenerator optimizedRandomGenerator = new OptimizedRandomGenerator(localeStore);

        // Act
        long startTime = System.currentTimeMillis();
        List<String> generatedPersons = IntStream.range(0, sampleSize)
                .mapToObj(i -> optimizedRandomGenerator.generate())
                .toList();
        long deficitTime = System.currentTimeMillis() - startTime;

        // Assert
        int size = generatedPersons.size();
        int sizeWithoutDuplicates = ((int) generatedPersons.stream().distinct().count());
        int numberOfDuplicates = size - sizeWithoutDuplicates;

        System.out.printf("For sample of size %d was generated %d duplicates!%n", size, numberOfDuplicates);
        System.out.printf("Execution time: %dms", deficitTime);
    }

    @ParameterizedTest
    @ValueSource(ints = { 100, 200, 500, 1000, 1500, 2000 })
    void SimpleGenerator_generatePersons_CountDuplicateNames(int sampleSize) {
        // Arrange

        LocaleStore localeStore = LocaleStoreFactory.getLocaleStore(Locale.ENGLISH);
        Generator generator = new RandomGenerator(localeStore);

        // Act
        long startTime = System.currentTimeMillis();
        List<Person> generatedPersons = IntStream.range(0, sampleSize)
                .mapToObj(i -> generator.generatePerson())
                .toList();
        long deficitTime = System.currentTimeMillis() - startTime;

        List<String> generatedNames = generatedPersons.stream()
                .map(person -> person.firstName() + localeStore.getNameDelimiter() + person.lastName())
                .toList();

        // Assert
        int size = generatedNames.size();
        int sizeWithoutDuplicates = ((int) generatedNames.stream().distinct().count());
        int numberOfDuplicates = size - sizeWithoutDuplicates;

        System.out.printf("For sample of size %d was generated %d duplicates!%n", size, numberOfDuplicates);
        System.out.printf("Execution time: %dms", deficitTime);
    }
}

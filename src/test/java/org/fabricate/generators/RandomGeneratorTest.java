package org.fabricate.generators;

import org.fabricate.model.Person;
import org.fabricate.store.LocaleStore;
import org.fabricate.store.LocaleStoreFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

class RandomGeneratorTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void generatePerson_GeneratesEnglishPersons_WhenEnglishFlagPassed() {
        // Arrange
        LocaleStore localeStore = LocaleStoreFactory.getLocaleStore(Locale.ENGLISH);
        RandomGenerator randomGenerator = new RandomGenerator(localeStore);

        // Act
        List<Person> generatedPersons = IntStream.range(0, 500)
                .mapToObj(i -> randomGenerator.generatePerson())
                .toList();

        // Assert
        generatedPersons.forEach(System.out::println);
    }
}
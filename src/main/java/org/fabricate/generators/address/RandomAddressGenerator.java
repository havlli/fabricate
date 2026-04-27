package org.fabricate.generators.address;

import org.fabricate.generators.strategies.GeneratorStrategy;
import org.fabricate.generators.strategies.SimpleRandomGeneratorStrategy;
import org.fabricate.model.Address;
import org.fabricate.store.LocaleStore;

public class RandomAddressGenerator implements AddressGenerator {

    private final LocaleStore localeStore;
    private final GeneratorStrategy randomGenerator;

    public RandomAddressGenerator(LocaleStore localeStore) {
        this.localeStore = localeStore;
        this.randomGenerator = new SimpleRandomGeneratorStrategy();
    }

    public RandomAddressGenerator(LocaleStore localeStore, GeneratorStrategy generatorStrategy) {
        this.localeStore = localeStore;
        this.randomGenerator = generatorStrategy;
    }

    @Override
    public String generateStreet() {
        return getRandomValueFrom(localeStore.getStreets());
    }

    @Override
    public String generateCity() {
        return getRandomValueFrom(localeStore.getCities());
    }

    @Override
    public String generateState() {
        return getRandomValueFrom(localeStore.getStates());
    }

    @Override
    public String generatePostalCode() {
        return getRandomValueFrom(localeStore.getPostalCodes());
    }

    @Override
    public String generateCountry() {
        return getRandomValueFrom(localeStore.getCountries());
    }

    @Override
    public Address generate() {
        return Address.builder()
                .street(generateStreet())
                .city(generateCity())
                .state(generateState())
                .postalCode(generatePostalCode())
                .country(generateCountry())
                .build();
    }

    private String getRandomValueFrom(String[] values) {
        return randomGenerator.generationStrategy()
                .apply(values);
    }
}

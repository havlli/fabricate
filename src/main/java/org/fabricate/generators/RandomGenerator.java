package org.fabricate.generators;

import org.fabricate.generators.address.RandomAddressGenerator;
import org.fabricate.generators.person.RandomPersonGenerator;
import org.fabricate.model.Address;
import org.fabricate.model.Person;
import org.fabricate.store.LocaleStore;

public class RandomGenerator implements Generator {

    private final RandomPersonGenerator personGenerator;
    private final RandomAddressGenerator addressGenerator;

    public RandomGenerator(LocaleStore localeStore) {
        this.personGenerator = new RandomPersonGenerator(localeStore);
        this.addressGenerator = new RandomAddressGenerator(localeStore);
    }

    @Override
    public Person generatePerson() {
        return personGenerator.generate();
    }

    @Override
    public Address generateAddress() {
        return addressGenerator.generate();
    }
}

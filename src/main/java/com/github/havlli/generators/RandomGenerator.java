package com.github.havlli.generators;

import com.github.havlli.generators.address.RandomAddressGenerator;
import com.github.havlli.generators.person.RandomPersonGenerator;
import com.github.havlli.model.Address;
import com.github.havlli.model.Person;
import com.github.havlli.store.LocaleStore;

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

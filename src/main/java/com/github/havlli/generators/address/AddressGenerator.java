package com.github.havlli.generators.address;

import com.github.havlli.model.Address;

public interface AddressGenerator {
    String generateStreet();
    String generateCity();
    String generateState();
    String generatePostalCode();
    String generateCountry();
    Address generate();
}

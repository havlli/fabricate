package org.fabricate.generators.address;

import org.fabricate.model.Address;

public interface AddressGenerator {
    String generateStreet();
    String generateCity();
    String generateState();
    String generatePostalCode();
    String generateCountry();
    Address generate();
}

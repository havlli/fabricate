package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.fabricate.locale.EnglishLocale;
import org.fabricate.model.Address;
import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class AddressesTest {

    @Test
    void address_drawsAllFieldsFromLocalePools() {
        Addresses addresses = new Addresses(EnglishLocale.INSTANCE, Rng.seeded(0L));

        Address address = addresses.address();

        assertThat(EnglishLocale.INSTANCE.streets()).contains(address.street());
        assertThat(EnglishLocale.INSTANCE.cities()).contains(address.city());
        assertThat(EnglishLocale.INSTANCE.states()).contains(address.state());
        assertThat(EnglishLocale.INSTANCE.postalCodes()).contains(address.postalCode());
        assertThat(EnglishLocale.INSTANCE.countries()).contains(address.country());
    }
}

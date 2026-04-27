package org.fabricate.provider;

import org.fabricate.model.Address;
import org.fabricate.random.Rng;
import org.fabricate.spi.LocaleData;

public final class Addresses {

    private final LocaleData locale;
    private final Rng rng;

    public Addresses(LocaleData locale, Rng rng) {
        this.locale = locale;
        this.rng = rng;
    }

    public String street()     { return rng.pick(locale.streets()); }
    public String city()       { return rng.pick(locale.cities()); }
    public String state()      { return rng.pick(locale.states()); }
    public String postalCode() { return rng.pick(locale.postalCodes()); }
    public String country()    { return rng.pick(locale.countries()); }

    public Address address() {
        return new Address(street(), city(), state(), postalCode(), country());
    }
}

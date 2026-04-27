package org.fabricate.provider;

import java.util.List;
import java.util.stream.Stream;
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

    /** Infinite stream of addresses. Use {@code .limit(n)} to bound. Not parallel-safe. */
    public Stream<Address> stream() {
        return Stream.generate(this::address);
    }

    /** Eagerly produces {@code count} addresses. */
    public List<Address> list(int count) {
        return stream().limit(count).toList();
    }
}

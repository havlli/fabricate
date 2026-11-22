package dev.havlli.fabricate.internal;

import java.util.Objects;
import java.util.function.Supplier;
import dev.havlli.fabricate.AddressGen;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.Gen;
import dev.havlli.fabricate.internal.provider.Addresses;
import dev.havlli.fabricate.model.Address;

public final class DefaultAddressGen extends DefaultGen<Address> implements AddressGen {

    private final Fabricate fab;
    private final Sources sources;

    public DefaultAddressGen(Fabricate fab, Addresses addresses) {
        this(fab, new Sources(
                addresses::street,
                addresses::city,
                addresses::state,
                addresses::postalCode,
                addresses::country));
    }

    private DefaultAddressGen(Fabricate fab, Sources sources) {
        super(() -> generate(sources), fab.random());
        this.fab = fab;
        this.sources = sources;
    }

    @Override
    public AddressGen street(String value) {
        Objects.requireNonNull(value, "value");
        return street(fab.gen(() -> value));
    }

    @Override
    public AddressGen street(Gen<? extends String> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultAddressGen(fab, new Sources(
                gen, sources.city, sources.state, sources.postalCode, sources.country));
    }

    @Override
    public AddressGen city(String value) {
        Objects.requireNonNull(value, "value");
        return city(fab.gen(() -> value));
    }

    @Override
    public AddressGen city(Gen<? extends String> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultAddressGen(fab, new Sources(
                sources.street, gen, sources.state, sources.postalCode, sources.country));
    }

    @Override
    public AddressGen state(String value) {
        Objects.requireNonNull(value, "value");
        return state(fab.gen(() -> value));
    }

    @Override
    public AddressGen state(Gen<? extends String> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultAddressGen(fab, new Sources(
                sources.street, sources.city, gen, sources.postalCode, sources.country));
    }

    @Override
    public AddressGen postalCode(String value) {
        Objects.requireNonNull(value, "value");
        return postalCode(fab.gen(() -> value));
    }

    @Override
    public AddressGen postalCode(Gen<? extends String> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultAddressGen(fab, new Sources(
                sources.street, sources.city, sources.state, gen, sources.country));
    }

    @Override
    public AddressGen country(String value) {
        Objects.requireNonNull(value, "value");
        return country(fab.gen(() -> value));
    }

    @Override
    public AddressGen country(Gen<? extends String> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultAddressGen(fab, new Sources(
                sources.street, sources.city, sources.state, sources.postalCode, gen));
    }

    private static Address generate(Sources sources) {
        return new Address(sources.street.get(), sources.city.get(), sources.state.get(),
                sources.postalCode.get(), sources.country.get());
    }

    private record Sources(
            Supplier<? extends String> street,
            Supplier<? extends String> city,
            Supplier<? extends String> state,
            Supplier<? extends String> postalCode,
            Supplier<? extends String> country
    ) {}
}

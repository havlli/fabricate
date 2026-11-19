package dev.havlli.fabricate;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import dev.havlli.fabricate.model.Address;
import dev.havlli.fabricate.model.Person;
import org.jspecify.annotations.Nullable;

final class DefaultPersonGen extends DefaultGen<Person> implements PersonGen {

    private final Fabricate fab;
    private final Sources sources;

    DefaultPersonGen(Fabricate fab) {
        this(fab, new Sources(
                fab.names()::firstName,
                fab.names()::lastName,
                null,
                fab.identities()::uuid,
                null,
                fab.phones()::phoneNumber,
                fab.addresses()::address,
                fab.datesOfBirth()::birthdate,
                fab.jobTitles()::jobTitle));
    }

    private DefaultPersonGen(Fabricate fab, Sources sources) {
        super(() -> generate(fab, sources), fab.rng());
        this.fab = fab;
        this.sources = sources;
    }

    @Override
    public PersonGen firstName(String value) {
        Objects.requireNonNull(value, "value");
        return firstName(fab.gen(() -> value));
    }

    @Override
    public PersonGen firstName(Gen<? extends String> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultPersonGen(fab, new Sources(
                gen, sources.lastName, sources.email,
                sources.uid, sources.username, sources.phoneNumber,
                sources.address, sources.dateOfBirth, sources.jobTitle));
    }

    @Override
    public PersonGen lastName(String value) {
        Objects.requireNonNull(value, "value");
        return lastName(fab.gen(() -> value));
    }

    @Override
    public PersonGen lastName(Gen<? extends String> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultPersonGen(fab, new Sources(
                sources.firstName, gen, sources.email,
                sources.uid, sources.username, sources.phoneNumber,
                sources.address, sources.dateOfBirth, sources.jobTitle));
    }

    @Override
    public PersonGen email(String value) {
        Objects.requireNonNull(value, "value");
        return email(fab.gen(() -> value));
    }

    @Override
    public PersonGen email(Gen<? extends String> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultPersonGen(fab, new Sources(
                sources.firstName, sources.lastName, gen,
                sources.uid, sources.username, sources.phoneNumber,
                sources.address, sources.dateOfBirth, sources.jobTitle));
    }

    @Override
    public PersonGen uid(UUID value) {
        Objects.requireNonNull(value, "value");
        return uid(fab.gen(() -> value));
    }

    @Override
    public PersonGen uid(Gen<? extends UUID> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultPersonGen(fab, new Sources(
                sources.firstName, sources.lastName, sources.email,
                gen, sources.username, sources.phoneNumber,
                sources.address, sources.dateOfBirth, sources.jobTitle));
    }

    @Override
    public PersonGen username(String value) {
        Objects.requireNonNull(value, "value");
        return username(fab.gen(() -> value));
    }

    @Override
    public PersonGen username(Gen<? extends String> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultPersonGen(fab, new Sources(
                sources.firstName, sources.lastName, sources.email,
                sources.uid, gen, sources.phoneNumber,
                sources.address, sources.dateOfBirth, sources.jobTitle));
    }

    @Override
    public PersonGen phoneNumber(String value) {
        Objects.requireNonNull(value, "value");
        return phoneNumber(fab.gen(() -> value));
    }

    @Override
    public PersonGen phoneNumber(Gen<? extends String> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultPersonGen(fab, new Sources(
                sources.firstName, sources.lastName, sources.email,
                sources.uid, sources.username, gen,
                sources.address, sources.dateOfBirth, sources.jobTitle));
    }

    @Override
    public PersonGen address(Address value) {
        Objects.requireNonNull(value, "value");
        return address(fab.gen(() -> value));
    }

    @Override
    public PersonGen address(Gen<? extends Address> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultPersonGen(fab, new Sources(
                sources.firstName, sources.lastName, sources.email,
                sources.uid, sources.username, sources.phoneNumber,
                gen, sources.dateOfBirth, sources.jobTitle));
    }

    @Override
    public PersonGen dateOfBirth(LocalDate value) {
        Objects.requireNonNull(value, "value");
        return dateOfBirth(fab.gen(() -> value));
    }

    @Override
    public PersonGen dateOfBirth(Gen<? extends LocalDate> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultPersonGen(fab, new Sources(
                sources.firstName, sources.lastName, sources.email,
                sources.uid, sources.username, sources.phoneNumber,
                sources.address, gen, sources.jobTitle));
    }

    @Override
    public PersonGen jobTitle(String value) {
        Objects.requireNonNull(value, "value");
        return jobTitle(fab.gen(() -> value));
    }

    @Override
    public PersonGen jobTitle(Gen<? extends String> gen) {
        Objects.requireNonNull(gen, "gen");
        return new DefaultPersonGen(fab, new Sources(
                sources.firstName, sources.lastName, sources.email,
                sources.uid, sources.username, sources.phoneNumber,
                sources.address, sources.dateOfBirth, gen));
    }

    @Override
    public PersonGen uid(String value) {
        Objects.requireNonNull(value, "value");
        UUID parsed;
        try {
            parsed = UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID: '" + value + "'", e);
        }
        return uid(parsed);
    }

    private static Person generate(Fabricate fab, Sources sources) {
        // Preserve Persons.Builder.build()'s order, including dependent defaults.
        String firstName = sources.firstName.get();
        String lastName = sources.lastName.get();
        String email = sources.email != null ? sources.email.get() : fab.emails().email(firstName, lastName);
        UUID uid = sources.uid.get();
        String username = sources.username != null ? sources.username.get()
                : fab.identities().username(firstName, lastName, uid);
        String phoneNumber = sources.phoneNumber.get();
        Address address = sources.address.get();
        LocalDate dateOfBirth = sources.dateOfBirth.get();
        String jobTitle = sources.jobTitle.get();
        return new Person(firstName, lastName, email, uid.toString(), username,
                phoneNumber, address, dateOfBirth, jobTitle);
    }

    private record Sources(
            Supplier<? extends String> firstName,
            Supplier<? extends String> lastName,
            @Nullable Supplier<? extends String> email,
            Supplier<? extends UUID> uid,
            @Nullable Supplier<? extends String> username,
            Supplier<? extends String> phoneNumber,
            Supplier<? extends Address> address,
            Supplier<? extends LocalDate> dateOfBirth,
            Supplier<? extends String> jobTitle
    ) {}
}

package dev.havlli.fabricate;

import java.time.LocalDate;
import java.util.UUID;
import dev.havlli.fabricate.model.Address;
import dev.havlli.fabricate.model.Person;

/**
 * A fluent source of {@link Person} records, bound to a {@link Fabricate}'s RNG.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} (or by composing those instances) and must not implement
 * this interface. Methods may be added in future versions.
 *
 * <p>Each override returns a new immutable configuration, leaving the receiver
 * unchanged. Repeating a field replaces its earlier override. Creating or
 * configuring a generator consumes no random values. An overridden field skips
 * default generation and consumes no RNG itself; a supplied generator is invoked
 * on each draw and may consume random values. Overrides therefore change the RNG
 * sequence for later fields. Derived generators share their RNG and override
 * sources and are not thread-safe.
 *
 * <p>Unless overridden, email is derived from the generated first and last name,
 * and username from those names and the generated uid.
 */
public interface PersonGen extends Gen<Person> {

    /**
     * Returns a new generator that uses a fixed first name.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    PersonGen firstName(String value);

    /**
     * Returns a new generator that draws the first name from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    PersonGen firstName(Gen<? extends String> gen);

    /**
     * Returns a new generator that uses a fixed last name.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    PersonGen lastName(String value);

    /**
     * Returns a new generator that draws the last name from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    PersonGen lastName(Gen<? extends String> gen);

    /**
     * Returns a new generator that uses a fixed email.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    PersonGen email(String value);

    /**
     * Returns a new generator that draws email from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    PersonGen email(Gen<? extends String> gen);

    /**
     * Returns a new generator that uses a fixed uid.
     * The UUID is stored as its {@link UUID#toString()} representation.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    PersonGen uid(UUID value);

    /**
     * Returns a new generator that draws uid from {@code gen} on each draw.
     * The UUID is stored as its {@link UUID#toString()} representation.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    PersonGen uid(Gen<? extends UUID> gen);

    /**
     * Returns a new generator that uses a fixed username.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    PersonGen username(String value);

    /**
     * Returns a new generator that draws username from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    PersonGen username(Gen<? extends String> gen);

    /**
     * Returns a new generator that uses a fixed phone number.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    PersonGen phoneNumber(String value);

    /**
     * Returns a new generator that draws the phone number from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    PersonGen phoneNumber(Gen<? extends String> gen);

    /**
     * Returns a new generator that uses a fixed address.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    PersonGen address(Address value);

    /**
     * Returns a new generator that draws address from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    PersonGen address(Gen<? extends Address> gen);

    /**
     * Returns a new generator that uses a fixed date of birth.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    PersonGen dateOfBirth(LocalDate value);

    /**
     * Returns a new generator that draws the date of birth from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    PersonGen dateOfBirth(Gen<? extends LocalDate> gen);

    /**
     * Returns a new generator that uses a fixed job title.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    PersonGen jobTitle(String value);

    /**
     * Returns a new generator that draws the job title from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    PersonGen jobTitle(Gen<? extends String> gen);

    /**
     * Returns a new generator with a uid parsed by {@link UUID#fromString(String)}
     * and stored as its {@link UUID#toString()} representation.
     *
     * @param value the UUID string
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     * @throws IllegalArgumentException if {@code value} cannot be parsed as a UUID;
     *         the message includes the bad value
     */
    PersonGen uid(String value);
}

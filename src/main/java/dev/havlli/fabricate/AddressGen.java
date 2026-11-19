package dev.havlli.fabricate;

import dev.havlli.fabricate.model.Address;

/**
 * A fluent source of {@link Address} records, bound to a {@link Fabricate}'s RNG.
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
 */
public interface AddressGen extends Gen<Address> {

    /**
     * Returns a new generator that uses a fixed street.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    AddressGen street(String value);

    /**
     * Returns a new generator that draws street from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    AddressGen street(Gen<? extends String> gen);

    /**
     * Returns a new generator that uses a fixed city.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    AddressGen city(String value);

    /**
     * Returns a new generator that draws city from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    AddressGen city(Gen<? extends String> gen);

    /**
     * Returns a new generator that uses a fixed state.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    AddressGen state(String value);

    /**
     * Returns a new generator that draws state from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    AddressGen state(Gen<? extends String> gen);

    /**
     * Returns a new generator that uses a fixed postal code.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    AddressGen postalCode(String value);

    /**
     * Returns a new generator that draws the postal code from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    AddressGen postalCode(Gen<? extends String> gen);

    /**
     * Returns a new generator that uses a fixed country.
     *
     * @param value the fixed value
     * @return a new generator with this override
     * @throws NullPointerException if {@code value} is null
     */
    AddressGen country(String value);

    /**
     * Returns a new generator that draws country from {@code gen} on each draw.
     *
     * @param gen the source generator
     * @return a new generator with this override
     * @throws NullPointerException if {@code gen} is null
     */
    AddressGen country(Gen<? extends String> gen);
}

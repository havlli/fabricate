package dev.havlli.fabricate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

/**
 * A source of dates of birth, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface DateOfBirthGen {

    /** Birthdate for a person aged in {@code [18, 90)} on the clock's "today". */
    LocalDate get();

    /**
     * Birthdate for a person aged in {@code [minAge, maxAgeExclusive)} today.
     *
     * @throws IllegalArgumentException if {@code minAge < 0} or
     *         {@code maxAgeExclusive <= minAge}.
     */
    LocalDate get(int minAge, int maxAgeExclusive);

    /** Infinite stream of birthdates using the default age range. Not parallel-safe. */
    Stream<LocalDate> stream();

    /** Eagerly produces {@code count} birthdates using the default age range. */
    List<LocalDate> list(int count);
}

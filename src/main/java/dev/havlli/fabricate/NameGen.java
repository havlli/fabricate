package dev.havlli.fabricate;

import java.util.List;
import java.util.stream.Stream;

/**
 * A source of name values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface NameGen {

    /** Draws a first name from the configured locale. */
    String first();

    /** Draws a last name from the configured locale. */
    String last();

    /** Draws a full name using the configured locale’s delimiter. */
    String full();

    /** Infinite stream of full names. Use {@code .limit(n)} to bound. Not parallel-safe. */
    Stream<String> stream();

    /** Eagerly produces {@code count} full names. */
    List<String> list(int count);
}

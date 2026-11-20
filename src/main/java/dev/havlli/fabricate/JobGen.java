package dev.havlli.fabricate;

import java.util.List;
import java.util.stream.Stream;

/**
 * A source of job values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface JobGen {

    /** Draws a job title from the configured locale. */
    String title();

    /** Infinite stream of job titles. Use {@code .limit(n)} to bound. Not parallel-safe. */
    Stream<String> stream();

    /** Eagerly produces {@code count} job titles. */
    List<String> list(int count);
}

package dev.havlli.fabricate;

/**
 * A source of boolean values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface BoolGen {

    /** Fair coin flip. */
    boolean get();

    /**
     * Returns {@code true} with the given probability {@code p}.
     *
     * @throws IllegalArgumentException if {@code p} is outside {@code [0, 1]}.
     */
    boolean withProbability(double p);
}

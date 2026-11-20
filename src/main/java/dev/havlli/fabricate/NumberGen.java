package dev.havlli.fabricate;

/**
 * A source of number values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface NumberGen {

    /** Uniform int in {@code [min, max]} (both inclusive). */
    int between(int min, int max);

    /** Uniform long in {@code [min, max]} (both inclusive). */
    long between(long min, long max);

    /** Uniform double in {@code [min, max)} (max exclusive). */
    double between(double min, double max);

    /** Gaussian-distributed double with the given mean and standard deviation. */
    double gaussian(double mean, double stddev);
}

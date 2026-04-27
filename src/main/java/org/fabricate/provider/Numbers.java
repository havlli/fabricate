package org.fabricate.provider;

import org.fabricate.random.Rng;

/**
 * Numeric primitives in fluent ranges.
 *
 * {@snippet :
 * int score      = fab.numbers().intBetween(0, 100);
 * long timestamp = fab.numbers().longBetween(1_000_000L, 2_000_000L);
 * double weight  = fab.numbers().doubleBetween(40.0, 90.0);
 * double iq      = fab.numbers().gaussian(100.0, 15.0);
 * }
 */
public final class Numbers {

    private final Rng rng;

    public Numbers(Rng rng) {
        this.rng = rng;
    }

    /** Uniform int in {@code [min, max]} (both inclusive). */
    public int intBetween(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("max must be >= min, was " + max + " < " + min);
        }
        long span = (long) max - (long) min + 1L;
        return (int) (min + rng.nextLong(0, span));
    }

    /** Uniform long in {@code [min, max]} (both inclusive). */
    public long longBetween(long min, long max) {
        if (max < min) {
            throw new IllegalArgumentException("max must be >= min, was " + max + " < " + min);
        }
        if (max == Long.MAX_VALUE && min == Long.MIN_VALUE) {
            return rng.delegate().nextLong();
        }
        return min + rng.nextLong(0, max - min + 1L);
    }

    /** Uniform double in {@code [min, max)} (max exclusive). */
    public double doubleBetween(double min, double max) {
        if (!(max > min)) {
            throw new IllegalArgumentException("max must be > min, was " + max + " <= " + min);
        }
        return min + rng.delegate().nextDouble() * (max - min);
    }

    /** Gaussian-distributed double with the given mean and standard deviation. */
    public double gaussian(double mean, double stddev) {
        if (stddev < 0) {
            throw new IllegalArgumentException("stddev must be >= 0, was " + stddev);
        }
        return mean + rng.delegate().nextGaussian() * stddev;
    }
}

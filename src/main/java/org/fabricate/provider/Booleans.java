package org.fabricate.provider;

import org.fabricate.random.Rng;

/**
 * Booleans, optionally biased toward {@code true}.
 *
 * {@snippet :
 * boolean coin       = fab.booleans().any();
 * boolean usuallyOn  = fab.booleans().withProbability(0.95);  // ~95% true
 * }
 */
public final class Booleans {

    private final Rng rng;

    public Booleans(Rng rng) {
        this.rng = rng;
    }

    /** Fair coin flip. */
    public boolean any() {
        return rng.delegate().nextBoolean();
    }

    /**
     * Returns {@code true} with the given probability {@code p}.
     *
     * @throws IllegalArgumentException if {@code p} is outside {@code [0, 1]}.
     */
    public boolean withProbability(double p) {
        if (!(p >= 0.0 && p <= 1.0)) {
            throw new IllegalArgumentException("probability must be in [0, 1], was " + p);
        }
        return rng.delegate().nextDouble() < p;
    }
}

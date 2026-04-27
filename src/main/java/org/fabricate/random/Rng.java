package org.fabricate.random;

import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * Thin wrapper around the JEP 356 {@link RandomGenerator} API.
 *
 * Centralises seed handling and the small set of distribution helpers the
 * built-in providers need, so providers don't reinvent the same idioms.
 *
 * Default backing algorithm is {@code L64X128MixRandom} — splittable, good
 * statistical quality, and stable across JDKs (Java 17+ guarantee).
 */
public final class Rng {

    private static final String ALGORITHM = "L64X128MixRandom";

    private final RandomGenerator delegate;

    private Rng(RandomGenerator delegate) {
        this.delegate = delegate;
    }

    public static Rng create() {
        return new Rng(RandomGeneratorFactory.of(ALGORITHM).create());
    }

    public static Rng seeded(long seed) {
        return new Rng(RandomGeneratorFactory.of(ALGORITHM).create(seed));
    }

    public RandomGenerator delegate() {
        return delegate;
    }

    public int nextInt(int boundExclusive) {
        if (boundExclusive <= 0) {
            throw new IllegalArgumentException("bound must be > 0, was " + boundExclusive);
        }
        return delegate.nextInt(boundExclusive);
    }

    public long nextLong(long origin, long boundExclusive) {
        return delegate.nextLong(origin, boundExclusive);
    }

    public <T> T pick(List<T> values) {
        Objects.requireNonNull(values, "values");
        if (values.isEmpty()) {
            throw new IllegalArgumentException("cannot pick from empty list");
        }
        return values.get(delegate.nextInt(values.size()));
    }
}

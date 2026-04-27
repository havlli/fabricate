package org.fabricate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.fabricate.random.Rng;

/**
 * Picks values from a finite set with caller-supplied weights.
 *
 * Weights need not sum to 1.0 — they are normalized internally — so callers
 * can express ratios directly:
 *
 * {@snippet :
 * WeightedPicker<String> status = WeightedPicker.<String>builder(fab.rng())
 *         .add("active",  80)
 *         .add("pending", 15)
 *         .add("banned",   5)
 *         .build();
 *
 * String s = status.pick();   // "active" most of the time
 * }
 *
 * Or for one-liners over a {@link Map}:
 *
 * {@snippet :
 * WeightedPicker<String> p = WeightedPicker.of(
 *         fab.rng(),
 *         Map.of("US", 0.7, "DE", 0.2, "JP", 0.1));
 * }
 *
 * Instances are immutable after construction; the underlying {@link Rng} is
 * the only thread-confined state.
 */
public final class WeightedPicker<T> {

    private final Rng rng;
    private final List<T> values;
    private final double[] cumulative;

    private WeightedPicker(Rng rng, List<T> values, double[] cumulative) {
        this.rng = rng;
        this.values = values;
        this.cumulative = cumulative;
    }

    public static <T> WeightedPicker<T> of(Rng rng, Map<T, ? extends Number> weights) {
        Builder<T> b = builder(rng);
        weights.forEach((value, weight) -> b.add(value, weight.doubleValue()));
        return b.build();
    }

    public static <T> Builder<T> builder(Rng rng) {
        return new Builder<>(rng);
    }

    public T pick() {
        double r = rng.delegate().nextDouble() * cumulative[cumulative.length - 1];
        // Linear scan is fine: typical N is small (< 20 buckets);
        // a binary search would barely move the needle and add complexity.
        for (int i = 0; i < cumulative.length; i++) {
            if (r < cumulative[i]) {
                return values.get(i);
            }
        }
        return values.get(values.size() - 1);
    }

    public static final class Builder<T> {

        private final Rng rng;
        private final List<T> values = new ArrayList<>();
        private final List<Double> weights = new ArrayList<>();

        private Builder(Rng rng) {
            this.rng = Objects.requireNonNull(rng, "rng");
        }

        public Builder<T> add(T value, double weight) {
            if (!(weight > 0.0) || Double.isInfinite(weight) || Double.isNaN(weight)) {
                throw new IllegalArgumentException(
                        "weight must be a finite positive number, was " + weight);
            }
            values.add(value);
            weights.add(weight);
            return this;
        }

        public WeightedPicker<T> build() {
            if (values.isEmpty()) {
                throw new IllegalStateException("WeightedPicker requires at least one entry");
            }
            double[] cumulative = new double[values.size()];
            double sum = 0.0;
            for (int i = 0; i < weights.size(); i++) {
                sum += weights.get(i);
                cumulative[i] = sum;
            }
            return new WeightedPicker<>(rng, List.copyOf(values), cumulative);
        }
    }
}

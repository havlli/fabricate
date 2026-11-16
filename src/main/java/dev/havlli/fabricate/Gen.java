package dev.havlli.fabricate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

/**
 * A fluent source of generated values, bound to a {@link Fabricate}'s RNG.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} (or by composing those instances) and must not implement
 * this interface. Methods may be added in future versions.
 *
 * <p>Generators are lazy until drawn from and are not thread-safe. Derived
 * generators share their source and RNG; each call to {@link #unique()} or
 * {@link #unique(int)} creates its own seen-set.
 *
 * @param <T> the generated value type, which may be nullable
 */
public interface Gen<T extends @Nullable Object> extends Supplier<T> {

    /** Draws the next value from the source. */
    @Override
    T get();

    /**
     * Draws {@code count} values in order into an immutable list, allowing nulls.
     *
     * @throws IllegalArgumentException if {@code count} is negative
     */
    List<T> list(int count);

    /** Returns an infinite, sequential stream that draws values on demand. */
    Stream<T> stream();

    /**
     * Draws {@code count} distinct values into an immutable set in insertion order,
     * allowing null. Each call tracks distinct values independently, using
     * {@link Unique#DEFAULT_RETRY_BUDGET} attempts per value.
     *
     * @throws IllegalArgumentException if {@code count} is negative
     * @throws UniqueExhaustedException if the retry budget is exhausted
     */
    Set<T> set(int count);

    /**
     * Returns a new generator with its own seen-set and
     * {@link Unique#DEFAULT_RETRY_BUDGET} attempts per value.
     * Draws throw {@link UniqueExhaustedException} when the budget is exhausted.
     */
    Gen<T> unique();

    /**
     * Returns a new generator with its own seen-set and {@code retryBudget}
     * attempts per value, including the initial attempt. Equality determines
     * uniqueness; null is treated as a distinct value. Draws throw
     * {@link UniqueExhaustedException} when the budget is exhausted.
     *
     * @throws IllegalArgumentException if {@code retryBudget} is less than one
     */
    Gen<T> unique(int retryBudget);

    /**
     * Returns a generator that draws from this source with probability
     * {@code presentProbability}, otherwise returning {@link Optional#empty()}.
     * Each draw consumes a probability check even at zero or one, just like
     * {@link Fabricate#optional(double, Supplier)}. A selected null source value
     * throws {@link NullPointerException}, as it does for that method.
     *
     * @throws IllegalArgumentException if the probability is outside {@code [0, 1]}
     *         or is NaN
     */
    Gen<Optional<T>> optional(double presentProbability);

    /**
     * Returns a generator that produces null with probability {@code nullProbability},
     * otherwise drawing from this source. Each draw consumes a probability check
     * even at zero or one.
     *
     * @throws IllegalArgumentException if the probability is outside {@code [0, 1]}
     *         or is NaN
     */
    Gen<@Nullable T> orNull(double nullProbability);

    /**
     * Returns a generator that applies {@code fn} to each value drawn from this source.
     * The function may return null.
     *
     * @param <R> the mapped value type
     * @param fn the mapping function
     * @return the mapped generator
     * @throws NullPointerException if {@code fn} is null
     */
    <R extends @Nullable Object> Gen<R> map(Function<? super T, ? extends R> fn);
}

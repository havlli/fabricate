package org.fabricate;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Decorator that draws from a source until it produces a value not yet seen.
 *
 * Useful when generating bulk data where collisions on locale-bound pools
 * (names, emails, addresses) are otherwise inevitable. The wrapper enforces
 * a retry budget so a finite pool can be drained predictably:
 *
 * {@snippet :
 * Fabricate fab = Fabricate.builder().seed(7L).build();
 * Unique<String> emails = Unique.of(fab.emails()::email);
 * List<String> distinct = emails.list(50);
 * }
 *
 * Instances are stateful and not thread-safe. Reuse the same {@code Unique}
 * for the duration of a generation pass; create a new one (or call
 * {@link #reset()}) when starting fresh.
 */
public final class Unique<T> {

    /** Default retry budget per draw before declaring the source exhausted. */
    public static final int DEFAULT_RETRY_BUDGET = 100;

    private final Supplier<T> source;
    private final int retryBudget;
    private final Set<T> seen = new HashSet<>();

    private Unique(Supplier<T> source, int retryBudget) {
        this.source = Objects.requireNonNull(source, "source");
        if (retryBudget < 1) {
            throw new IllegalArgumentException("retryBudget must be >= 1, was " + retryBudget);
        }
        this.retryBudget = retryBudget;
    }

    public static <T> Unique<T> of(Supplier<T> source) {
        return new Unique<>(source, DEFAULT_RETRY_BUDGET);
    }

    public static <T> Unique<T> of(Supplier<T> source, int retryBudget) {
        return new Unique<>(source, retryBudget);
    }

    /**
     * Draws until a fresh value is found, or throws {@link UniqueExhaustedException}
     * if the retry budget is consumed without success.
     */
    public T get() {
        for (int attempt = 0; attempt < retryBudget; attempt++) {
            T candidate = source.get();
            if (seen.add(candidate)) {
                return candidate;
            }
        }
        throw new UniqueExhaustedException(seen.size(), retryBudget);
    }

    /** Infinite stream of unique values. Throws if the source exhausts. */
    public Stream<T> stream() {
        return Stream.generate(this::get);
    }

    /** Eagerly produces {@code count} unique values. */
    public List<T> list(int count) {
        return stream().limit(count).toList();
    }

    public int seenCount() {
        return seen.size();
    }

    /** Forgets all previously-seen values; subsequent draws may repeat earlier results. */
    public void reset() {
        seen.clear();
    }
}

package dev.havlli.fabricate;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import dev.havlli.fabricate.random.Rng;
import org.jspecify.annotations.Nullable;

final class DefaultGen<T extends @Nullable Object> implements Gen<T> {

    private final Supplier<? extends T> source;
    private final Rng rng;

    DefaultGen(Supplier<? extends T> source, Rng rng) {
        this.source = Objects.requireNonNull(source, "source");
        this.rng = Objects.requireNonNull(rng, "rng");
    }

    @Override
    public T get() {
        return source.get();
    }

    @Override
    public List<T> list(int count) {
        checkCount(count);
        return stream().limit(count).toList();
    }

    @Override
    public Stream<T> stream() {
        return Stream.generate(this);
    }

    @Override
    public Set<T> set(int count) {
        checkCount(count);
        Gen<T> distinct = unique();
        Set<T> values = new LinkedHashSet<>();
        for (int i = 0; i < count; i++) {
            values.add(distinct.get());
        }
        return Collections.unmodifiableSet(values);
    }

    @Override
    public Gen<T> unique() {
        return unique(Unique.DEFAULT_RETRY_BUDGET);
    }

    @Override
    public Gen<T> unique(int retryBudget) {
        // Keep Unique's non-null type bound while allowing nullable generated values.
        Unique<Value<T>> distinct = Unique.of(() -> new Value<>(get()), retryBudget);
        return new DefaultGen<>(() -> distinct.get().value(), rng);
    }

    @Override
    public Gen<Optional<T>> optional(double presentProbability) {
        checkProbability(presentProbability);
        return new DefaultGen<>(() -> rng.delegate().nextDouble() < presentProbability
                ? Optional.of(Objects.requireNonNull(get(), "source result"))
                : Optional.empty(), rng);
    }

    @Override
    public Gen<@Nullable T> orNull(double nullProbability) {
        checkProbability(nullProbability);
        return new DefaultGen<>(() -> rng.delegate().nextDouble() < nullProbability
                ? null : get(), rng);
    }

    @Override
    public <R extends @Nullable Object> Gen<R> map(Function<? super T, ? extends R> fn) {
        Objects.requireNonNull(fn, "fn");
        return new DefaultGen<>(() -> fn.apply(get()), rng);
    }

    private static void checkCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must be >= 0");
        }
    }

    private static void checkProbability(double probability) {
        if (!(probability >= 0.0 && probability <= 1.0)) {
            throw new IllegalArgumentException("probability must be in [0, 1], was " + probability);
        }
    }

    private record Value<T extends @Nullable Object>(T value) {}
}

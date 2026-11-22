package dev.havlli.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GenTest {

    private static Fabricate seeded() {
        return Fabricate.builder().seed(42L).build();
    }

    @Test
    void get_delegatesLazilyAndIsASupplier() {
        AtomicInteger calls = new AtomicInteger();
        Gen<Integer> gen = seeded().gen(calls::incrementAndGet);
        Supplier<Integer> supplier = gen;

        assertThat(calls.get()).isZero();
        assertThat(gen.get()).isEqualTo(1);
        assertThat(supplier.get()).isEqualTo(2);
        assertThat(calls.get()).isEqualTo(2);
    }

    @Test
    void list_drawsExactlyCountValuesInOrderAndIsImmutable() {
        AtomicInteger calls = new AtomicInteger();
        Gen<Integer> gen = seeded().gen(calls::incrementAndGet);

        List<Integer> values = gen.list(3);

        assertThat(values).containsExactly(1, 2, 3);
        assertThat(calls.get()).isEqualTo(3);
        assertThatThrownBy(() -> values.add(4)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> values.set(0, 4)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> values.remove(0)).isInstanceOf(UnsupportedOperationException.class);
        gen.get();
        assertThat(values).containsExactly(1, 2, 3);
    }

    @Test
    void zeroCounts_doNotDrawAndReturnImmutableEmptyCollections() {
        AtomicInteger calls = new AtomicInteger();
        Gen<Integer> gen = seeded().gen(calls::incrementAndGet);

        List<Integer> list = gen.list(0);
        Set<Integer> set = gen.set(0);

        assertThat(list).isEmpty();
        assertThat(set).isEmpty();
        assertThat(calls.get()).isZero();
        assertThatThrownBy(() -> list.add(1)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> set.add(1)).isInstanceOf(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, Integer.MIN_VALUE})
    void negativeCounts_failWithoutDrawing(int count) {
        AtomicInteger calls = new AtomicInteger();
        Gen<Integer> gen = seeded().gen(calls::incrementAndGet);

        assertThatThrownBy(() -> gen.list(count)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> gen.set(count)).isInstanceOf(IllegalArgumentException.class);
        assertThat(calls.get()).isZero();
    }

    @Test
    void stream_isLazySequentialAndUnbounded() {
        AtomicInteger calls = new AtomicInteger();
        Gen<Integer> gen = seeded().gen(calls::incrementAndGet);

        try (Stream<Integer> stream = gen.stream()) {
            assertThat(calls.get()).isZero();
            assertThat(stream.isParallel()).isFalse();
            assertThat(stream.limit(1_000).toList()).hasSize(1_000).startsWith(1, 2).endsWith(1_000);
        }
        assertThat(calls.get()).isEqualTo(1_000);
        assertThat(gen.get()).isEqualTo(1_001);
    }

    @Test
    void set_skipsDuplicatesAndPreservesInsertionOrderAndIsImmutable() {
        List<String> source = List.of("b", "b", "a", "b", "c");
        AtomicInteger calls = new AtomicInteger();
        Gen<String> gen = seeded().gen(() -> source.get(calls.getAndIncrement() % source.size()));

        Set<String> values = gen.set(3);

        assertThat(values).containsExactly("b", "a", "c");
        assertThat(calls.get()).isEqualTo(5);
        assertThatThrownBy(() -> values.add("d")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> values.remove("b")).isInstanceOf(UnsupportedOperationException.class);
        var iterator = values.iterator();
        iterator.next();
        assertThatThrownBy(iterator::remove).isInstanceOf(UnsupportedOperationException.class);
        assertThat(gen.set(3)).containsExactly("b", "a", "c");
        assertThat(values).containsExactly("b", "a", "c");
    }

    @Test
    void set_exhaustionUsesUniqueDefaultBudgetPerValue() {
        AtomicInteger calls = new AtomicInteger();
        Gen<String> gen = seeded().gen(() -> {
            calls.incrementAndGet();
            return "same";
        });

        assertThatThrownBy(() -> gen.set(2))
                .isInstanceOfSatisfying(UniqueExhaustedException.class, ex -> {
                    assertThat(ex.seenCount()).isEqualTo(1);
                    assertThat(ex.retryBudget()).isEqualTo(Unique.DEFAULT_RETRY_BUDGET);
                });
        assertThat(calls.get()).isEqualTo(1 + Unique.DEFAULT_RETRY_BUDGET);
    }

    @Test
    void unique_instancesHaveIndependentSeenSets() {
        Gen<String> source = seeded().gen(() -> new String("same"));
        Gen<String> first = source.unique();
        Gen<String> second = source.unique();

        assertThat(first).isNotSameAs(second).isNotSameAs(source);
        assertThat(first.get()).isEqualTo("same");
        assertThatThrownBy(first::get).isInstanceOf(UniqueExhaustedException.class);
        assertThat(second.get()).isEqualTo("same");
        assertThatThrownBy(second::get).isInstanceOf(UniqueExhaustedException.class);
        assertThat(source.get()).isEqualTo("same");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, Unique.DEFAULT_RETRY_BUDGET})
    void unique_honorsRetryBudgetAndReportsExhaustion(int budget) {
        AtomicInteger calls = new AtomicInteger();
        Gen<Integer> gen = seeded().gen(() -> {
            calls.incrementAndGet();
            return 7;
        }).unique(budget);

        assertThat(calls.get()).isZero();
        assertThat(gen.get()).isEqualTo(7);
        assertThatThrownBy(gen::get)
                .isInstanceOfSatisfying(UniqueExhaustedException.class, ex -> {
                    assertThat(ex.seenCount()).isEqualTo(1);
                    assertThat(ex.retryBudget()).isEqualTo(budget);
                });
        assertThat(calls.get()).isEqualTo(1 + budget);
    }

    @Test
    void unique_retryBudgetResetsAfterEachSuccessfulDraw() {
        List<Integer> values = List.of(1, 1, 2, 2, 3);
        AtomicInteger index = new AtomicInteger();
        Gen<Integer> gen = seeded().gen(() -> values.get(index.getAndIncrement())).unique(2);

        assertThat(gen.list(3)).containsExactly(1, 2, 3);
        assertThat(index.get()).isEqualTo(5);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void unique_rejectsInvalidBudgetImmediately(int budget) {
        assertThatThrownBy(() -> seeded().gen(() -> "x").unique(budget))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void optional_andOrNullAreLazyAndOnlyDrawSourceWhenSelected() {
        AtomicInteger calls = new AtomicInteger();
        Gen<Integer> source = seeded().gen(calls::incrementAndGet);
        Gen<Optional<Integer>> absent = source.optional(0.0);
        Gen<Optional<Integer>> present = source.optional(1.0);
        Gen<@Nullable Integer> nulls = source.orNull(1.0);
        Gen<@Nullable Integer> nonNulls = source.orNull(0.0);

        assertThat(calls.get()).isZero();
        assertThat(absent.get()).isEmpty();
        assertThat(nulls.get()).isNull();
        assertThat(calls.get()).isZero();
        assertThat(present.get()).contains(1);
        assertThat(nonNulls.get()).isEqualTo(2);
        assertThat(calls.get()).isEqualTo(2);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, 1.01, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY})
    void probabilities_areValidatedLikeBooleansWithoutConsumingRng(double probability) {
        Fabricate fab = seeded();
        Gen<String> source = fab.gen(() -> "x");

        assertThatThrownBy(() -> source.optional(probability))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("probability must be in [0, 1], was " + probability);
        assertThatThrownBy(() -> source.orNull(probability))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("probability must be in [0, 1], was " + probability);
        assertThat(fab.random().nextLong()).isEqualTo(seeded().random().nextLong());
    }

    @Test
    void map_isLazyAndSupportsVariantTypesAndNullableResults() {
        AtomicInteger calls = new AtomicInteger();
        Supplier<Integer> source = calls::incrementAndGet;
        Gen<Number> numbers = seeded().gen(source);
        Function<Object, String> stringify = Object::toString;
        Gen<CharSequence> mapped = numbers.map(stringify);

        assertThat(calls.get()).isZero();
        assertThat(mapped.list(3)).containsExactly("1", "2", "3");
        assertThat(calls.get()).isEqualTo(3);
        Gen<@Nullable String> nulls = numbers.map(value -> null);
        assertThat(nulls.list(2)).containsExactly(null, null);
        assertThat(calls.get()).isEqualTo(5);
    }

    @Test
    void nullableValues_workWithListsSetsUniquenessAndMapping() {
        Gen<@Nullable String> nulls = seeded().gen(() -> null);

        assertThat(nulls.list(2)).containsExactly(null, null);
        assertThat(nulls.set(1)).containsExactly((String) null);
        Gen<@Nullable String> unique = nulls.unique(1);
        assertThat(unique.get()).isNull();
        assertThatThrownBy(unique::get).isInstanceOf(UniqueExhaustedException.class);
        assertThat(nulls.map(value -> value == null ? "missing" : value).get()).isEqualTo("missing");
        assertThat(nulls.optional(0.0).get()).isEmpty();
        assertThatThrownBy(() -> nulls.optional(1.0).get()).isInstanceOf(NullPointerException.class);
        assertThat(nulls.orNull(0.0).get()).isNull();
    }

    @Test
    void nullArguments_areRejectedWithParameterNames() {
        Fabricate fab = seeded();

        assertThatThrownBy(() -> fab.gen(null)).isInstanceOf(NullPointerException.class).hasMessage("source");
        assertThatThrownBy(() -> fab.oneOf((String[]) null))
                .isInstanceOf(NullPointerException.class).hasMessage("values");
        assertThatThrownBy(() -> fab.oneOf((Collection<String>) null))
                .isInstanceOf(NullPointerException.class).hasMessage("values");
        assertThatThrownBy(() -> fab.gen(() -> "x").map(null))
                .isInstanceOf(NullPointerException.class).hasMessage("fn");
    }

    @Test
    void oneOf_rejectsEmptyInputsImmediately() {
        Fabricate fab = seeded();

        assertThatThrownBy(() -> fab.oneOf()).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> fab.oneOf(List.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void oneOf_snapshotsInputsAndAcceptsSingletonsNullsAndVariantCollections() {
        Fabricate fab = seeded();
        String[] array = {"a"};
        Collection<Integer> collection = new ArrayList<>(List.of(7));
        Gen<String> fromArray = fab.oneOf(array);
        Gen<Number> fromCollection = fab.oneOf(collection);
        array[0] = "b";
        collection.clear();

        assertThat(fromArray.list(3)).containsExactly("a", "a", "a");
        assertThat(fromCollection.list(3)).containsExactly(7, 7, 7);
        assertThat(fab.oneOf((@Nullable String) null).get()).isNull();
        Collection<@Nullable String> nulls = new ArrayList<>();
        nulls.add(null);
        assertThat(fab.oneOf(nulls).get()).isNull();
    }

    @Test
    void list_matchesRepeatDrawForDraw() {
        Fabricate fluent = seeded();
        Fabricate classic = seeded();

        assertThat(fluent.gen(fluent.name()::full).list(100))
                .isEqualTo(classic.repeat(100, classic.name()::full));
        assertThat(fluent.random().nextLong()).isEqualTo(classic.random().nextLong());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.0, 0.0, 0.35, 1.0})
    void optional_matchesExistingHelperIncludingRngConsumption(double probability) {
        Fabricate fluent = seeded();
        Fabricate classic = seeded();
        Gen<Optional<String>> gen = fluent.gen(fluent.name()::full).optional(probability);

        for (int i = 0; i < 100; i++) {
            assertThat(gen.get()).isEqualTo(classic.optional(probability, classic.name()::full));
        }
        assertThat(fluent.random().nextLong()).isEqualTo(classic.random().nextLong());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.0, 0.0, 0.35, 1.0})
    void orNull_matchesBooleanProbabilityIncludingRngConsumption(double probability) {
        Fabricate fluent = seeded();
        Fabricate classic = seeded();
        Gen<@Nullable String> gen = fluent.gen(fluent.name()::full).orNull(probability);

        for (int i = 0; i < 100; i++) {
            String expected = classic.bool().withProbability(probability)
                    ? null : classic.name().full();
            assertThat(gen.get()).isEqualTo(expected);
        }
        assertThat(fluent.random().nextLong()).isEqualTo(classic.random().nextLong());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 7, 16})
    void oneOf_varargsMatchesVarargsChoiceDrawForDraw(int poolSize) {
        Fabricate fluent = seeded();
        Fabricate classic = seeded();
        // Duplicates remain separate choices, preserving their weighting.
        Integer[] values = Stream.iterate(0, i -> i + 1).limit(poolSize).map(i -> i / 2).toArray(Integer[]::new);
        Gen<Integer> gen = fluent.oneOf(values);

        for (int i = 0; i < 100; i++) {
            assertThat(gen.get()).isEqualTo(classic.choice(values));
        }
        assertThat(fluent.random().nextLong()).isEqualTo(classic.random().nextLong());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 7, 16})
    void oneOf_collectionMatchesListChoiceDrawForDraw(int poolSize) {
        Fabricate fluent = seeded();
        Fabricate classic = seeded();
        Collection<Integer> values = new LinkedHashSet<>(Stream.iterate(0, i -> i + 1).limit(poolSize).toList());
        List<Integer> ordered = new ArrayList<>(values);
        Gen<Integer> gen = fluent.oneOf(values);

        for (int i = 0; i < 100; i++) {
            assertThat(gen.get()).isEqualTo(classic.choice(ordered));
        }
        assertThat(fluent.random().nextLong()).isEqualTo(classic.random().nextLong());
    }

    @Test
    void composition_isReproducibleWithTheSameSeed() {
        Fabricate first = seeded();
        Fabricate second = seeded();

        assertThat(first.gen(first.name()::full).unique().map(String::length).optional(0.7).list(100))
                .isEqualTo(second.gen(second.name()::full).unique().map(String::length).optional(0.7).list(100));
        assertThat(first.oneOf("PAID", "OPEN", "CLOSED").orNull(0.3).list(100))
                .isEqualTo(second.oneOf("PAID", "OPEN", "CLOSED").orNull(0.3).list(100));
        assertThat(first.gen(first.name()::full).set(20))
                .containsExactlyElementsOf(second.gen(second.name()::full).set(20));
    }

    @Test
    void sourceAndMapperExceptions_propagateUnchanged() {
        RuntimeException failure = new IllegalStateException("source failed");
        Gen<String> failing = seeded().gen(() -> { throw failure; });

        assertThatThrownBy(failing::get).isSameAs(failure);
        assertThatThrownBy(() -> seeded().gen(() -> "x").map(value -> { throw failure; }).get())
                .isSameAs(failure);
    }
}

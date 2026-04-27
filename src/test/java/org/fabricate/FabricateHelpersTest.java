package org.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class FabricateHelpersTest {

    @Test
    void choice_varargs_picksOneOfTheValues() {
        Fabricate fab = Fabricate.builder().seed(1L).build();

        for (int i = 0; i < 100; i++) {
            String picked = fab.choice("a", "b", "c");
            assertThat(picked).isIn("a", "b", "c");
        }
    }

    @Test
    void choice_list_picksOneOfTheValues() {
        Fabricate fab = Fabricate.builder().seed(1L).build();
        List<Integer> options = List.of(10, 20, 30);

        for (int i = 0; i < 100; i++) {
            assertThat(fab.choice(options)).isIn(10, 20, 30);
        }
    }

    @Test
    void choice_emptyVarargs_throws() {
        Fabricate fab = Fabricate.builder().seed(1L).build();

        assertThatThrownBy(() -> fab.choice())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void optional_zeroProbability_alwaysEmpty() {
        Fabricate fab = Fabricate.builder().seed(1L).build();

        for (int i = 0; i < 100; i++) {
            assertThat(fab.optional(0.0, () -> "x")).isEmpty();
        }
    }

    @Test
    void optional_oneProbability_alwaysPresent() {
        Fabricate fab = Fabricate.builder().seed(1L).build();

        for (int i = 0; i < 100; i++) {
            Optional<String> v = fab.optional(1.0, () -> "x");
            assertThat(v).contains("x");
        }
    }

    @Test
    void sample_returnsRequestedCountWithoutDuplicates() {
        Fabricate fab = Fabricate.builder().seed(1L).build();
        List<Integer> pool = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<Integer> sampled = fab.sample(pool, 5);

        assertThat(sampled).hasSize(5);
        assertThat(sampled).doesNotHaveDuplicates();
        assertThat(pool).containsAll(sampled);
    }

    @Test
    void sample_zeroIsEmpty() {
        Fabricate fab = Fabricate.builder().seed(1L).build();
        assertThat(fab.sample(List.of(1, 2, 3), 0)).isEmpty();
    }

    @Test
    void sample_overSizeThrows() {
        Fabricate fab = Fabricate.builder().seed(1L).build();
        assertThatThrownBy(() -> fab.sample(List.of(1, 2), 5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shuffle_preservesElementsButChangesOrder() {
        Fabricate fab = Fabricate.builder().seed(1L).build();
        List<Integer> input = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<Integer> shuffled = fab.shuffle(input);

        assertThat(shuffled).hasSameElementsAs(input);
        assertThat(shuffled).isNotEqualTo(input);
    }

    @Test
    void repeat_callsSupplierExactlyCountTimes() {
        Fabricate fab = Fabricate.builder().seed(1L).build();
        java.util.concurrent.atomic.AtomicInteger calls = new java.util.concurrent.atomic.AtomicInteger();

        List<Integer> out = fab.repeat(7, calls::incrementAndGet);

        assertThat(out).hasSize(7);
        assertThat(calls.get()).isEqualTo(7);
        assertThat(out).containsExactly(1, 2, 3, 4, 5, 6, 7);
    }

    @Test
    void weighted_buildsWorkingPicker() {
        Fabricate fab = Fabricate.builder().seed(1L).build();
        WeightedPicker<String> picker = fab.<String>weighted()
                .add("yes", 9)
                .add("no", 1)
                .build();

        int yes = 0;
        for (int i = 0; i < 1_000; i++) {
            if (picker.pick().equals("yes")) yes++;
        }

        assertThat(yes).isBetween(820, 980);
    }
}

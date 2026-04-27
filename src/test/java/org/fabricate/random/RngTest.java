package org.fabricate.random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class RngTest {

    @Test
    void seeded_isReproducible() {
        Rng a = Rng.seeded(123L);
        Rng b = Rng.seeded(123L);

        for (int i = 0; i < 100; i++) {
            assertThat(a.nextInt(1_000_000)).isEqualTo(b.nextInt(1_000_000));
        }
    }

    @Test
    void create_producesDifferentSequencesAcrossInstances() {
        Rng a = Rng.create();
        Rng b = Rng.create();

        boolean differs = false;
        for (int i = 0; i < 100 && !differs; i++) {
            differs = a.nextInt(Integer.MAX_VALUE) != b.nextInt(Integer.MAX_VALUE);
        }
        assertThat(differs).isTrue();
    }

    @Test
    void pick_drawsFromList() {
        List<String> values = List.of("a", "b", "c");
        Rng rng = Rng.seeded(0L);

        for (int i = 0; i < 50; i++) {
            assertThat(values).contains(rng.pick(values));
        }
    }

    @Test
    void pick_emptyList_throws() {
        Rng rng = Rng.seeded(0L);

        assertThatThrownBy(() -> rng.pick(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nextInt_nonPositiveBound_throws() {
        Rng rng = Rng.seeded(0L);

        assertThatThrownBy(() -> rng.nextInt(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> rng.nextInt(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

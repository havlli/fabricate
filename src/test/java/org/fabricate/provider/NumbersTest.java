package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class NumbersTest {

    @Test
    void intBetween_isWithinClosedRange() {
        Numbers numbers = new Numbers(Rng.seeded(0L));

        for (int i = 0; i < 1000; i++) {
            assertThat(numbers.intBetween(5, 10)).isBetween(5, 10);
        }
    }

    @Test
    void intBetween_canCoverFullIntRange() {
        Numbers numbers = new Numbers(Rng.seeded(0L));

        // Just exercise extreme bounds without overflow.
        int v = numbers.intBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertThat(v).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Test
    void intBetween_singleValue_returnsThatValue() {
        Numbers numbers = new Numbers(Rng.seeded(0L));

        assertThat(numbers.intBetween(7, 7)).isEqualTo(7);
    }

    @Test
    void longBetween_isWithinClosedRange() {
        Numbers numbers = new Numbers(Rng.seeded(0L));

        for (int i = 0; i < 1000; i++) {
            assertThat(numbers.longBetween(-100L, 100L)).isBetween(-100L, 100L);
        }
    }

    @Test
    void doubleBetween_isWithinHalfOpenRange() {
        Numbers numbers = new Numbers(Rng.seeded(0L));

        for (int i = 0; i < 1000; i++) {
            double v = numbers.doubleBetween(0.0, 1.0);
            assertThat(v).isGreaterThanOrEqualTo(0.0).isLessThan(1.0);
        }
    }

    @Test
    void gaussian_isCloseToMeanOnAverage() {
        Numbers numbers = new Numbers(Rng.seeded(0L));
        double sum = 0.0;
        int n = 10_000;

        for (int i = 0; i < n; i++) {
            sum += numbers.gaussian(50.0, 5.0);
        }
        double mean = sum / n;

        // 5σ / sqrt(10000) = 0.05; allow a generous 0.5 envelope.
        assertThat(mean).isBetween(49.5, 50.5);
    }

    @Test
    void invalidRanges_throw() {
        Numbers numbers = new Numbers(Rng.seeded(0L));

        assertThatThrownBy(() -> numbers.intBetween(10, 5)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> numbers.longBetween(10L, 5L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> numbers.doubleBetween(1.0, 1.0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> numbers.gaussian(0.0, -1.0)).isInstanceOf(IllegalArgumentException.class);
    }
}

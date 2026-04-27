package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class BooleansTest {

    @Test
    void any_isApproximatelyFair() {
        Booleans booleans = new Booleans(Rng.seeded(0L));
        int trues = 0;
        int n = 10_000;

        for (int i = 0; i < n; i++) {
            if (booleans.any()) trues++;
        }

        assertThat(trues).isBetween(4500, 5500);
    }

    @Test
    void withProbability_zero_neverReturnsTrue() {
        Booleans booleans = new Booleans(Rng.seeded(0L));

        for (int i = 0; i < 1000; i++) {
            assertThat(booleans.withProbability(0.0)).isFalse();
        }
    }

    @Test
    void withProbability_one_alwaysReturnsTrue() {
        Booleans booleans = new Booleans(Rng.seeded(0L));

        for (int i = 0; i < 1000; i++) {
            assertThat(booleans.withProbability(1.0)).isTrue();
        }
    }

    @Test
    void withProbability_skewsResults() {
        Booleans booleans = new Booleans(Rng.seeded(0L));
        int trues = 0;
        int n = 10_000;

        for (int i = 0; i < n; i++) {
            if (booleans.withProbability(0.9)) trues++;
        }

        assertThat(trues).isBetween(8500, 9500);
    }

    @Test
    void invalidProbability_throws() {
        Booleans booleans = new Booleans(Rng.seeded(0L));

        assertThatThrownBy(() -> booleans.withProbability(-0.01))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> booleans.withProbability(1.01))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

package org.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class WeightedPickerTest {

    @Test
    void builder_distributesAccordingToWeights() {
        WeightedPicker<String> picker = WeightedPicker.<String>builder(Rng.seeded(0L))
                .add("active", 80)
                .add("pending", 15)
                .add("banned", 5)
                .build();

        Map<String, Integer> counts = new HashMap<>();
        int n = 10_000;
        for (int i = 0; i < n; i++) {
            counts.merge(picker.pick(), 1, Integer::sum);
        }

        assertThat(counts.get("active")).isBetween(7500, 8500);
        assertThat(counts.get("pending")).isBetween(1200, 1800);
        assertThat(counts.get("banned")).isBetween(350, 700);
    }

    @Test
    void of_withMap_normalizesWeights() {
        // Weights don't sum to 1; ensure normalization works.
        WeightedPicker<String> picker = WeightedPicker.of(
                Rng.seeded(1L),
                Map.of("a", 7.0, "b", 3.0));

        Map<String, Integer> counts = new HashMap<>();
        int n = 10_000;
        for (int i = 0; i < n; i++) {
            counts.merge(picker.pick(), 1, Integer::sum);
        }

        assertThat(counts.get("a")).isBetween(6500, 7500);
        assertThat(counts.get("b")).isBetween(2500, 3500);
    }

    @Test
    void emptyBuilder_throws() {
        assertThatThrownBy(() -> WeightedPicker.<String>builder(Rng.seeded(0L)).build())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void invalidWeight_throws() {
        WeightedPicker.Builder<String> b = WeightedPicker.builder(Rng.seeded(0L));

        assertThatThrownBy(() -> b.add("x", 0.0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> b.add("x", -1.0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> b.add("x", Double.NaN)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> b.add("x", Double.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

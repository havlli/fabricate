package org.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class TrialsTest {

    @Test
    void run_executesBodyExactlyNTimes() {
        AtomicInteger calls = new AtomicInteger();

        Trials.run(50, fab -> calls.incrementAndGet());

        assertThat(calls.get()).isEqualTo(50);
    }

    @Test
    void run_failureReportsFailingSeed() {
        assertThatThrownBy(() -> Trials.run(20, fab -> {
            String name = fab.names().firstName();
            if (name.length() > 0) {
                throw new RuntimeException("boom on " + name);
            }
        }))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("seed=0");
    }

    @Test
    void run_passingThrough_doesNotFail() {
        Trials.run(10, fab -> {
            assertThat(fab.names().firstName()).isNotBlank();
        });
    }

    @Test
    void run_zeroTrials_isANoOp() {
        Trials.run(0, fab -> { throw new AssertionError("should not run"); });
    }

    @Test
    void run_rejectsNegativeTrials() {
        assertThatThrownBy(() -> Trials.run(-1, fab -> {}))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void run_withLocale_buildsFabricateForThatLocale() {
        Trials.run(5, Locale.GERMAN, fab -> {
            assertThat(fab.locale().locale()).isEqualTo(Locale.GERMAN);
        });
    }

    @Test
    void replay_runsExactlyOnce() {
        AtomicInteger calls = new AtomicInteger();

        Trials.replay(7L, fab -> calls.incrementAndGet());

        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    void run_isDeterministicForSameSeedRange() {
        java.util.List<String> firstRun = new java.util.ArrayList<>();
        java.util.List<String> secondRun = new java.util.ArrayList<>();

        Trials.run(10, fab -> firstRun.add(fab.emails().email()));
        Trials.run(10, fab -> secondRun.add(fab.emails().email()));

        assertThat(firstRun).isEqualTo(secondRun);
    }
}

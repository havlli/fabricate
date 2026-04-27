package org.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class UniqueTest {

    @Test
    void list_returnsDistinctValues() {
        Fabricate fab = Fabricate.builder().seed(7L).build();
        Unique<String> emails = Unique.of(fab.emails()::email);

        List<String> distinct = emails.list(20);

        assertThat(distinct).hasSize(20).doesNotHaveDuplicates();
        assertThat(emails.seenCount()).isEqualTo(20);
    }

    @Test
    void exhaustedSource_throwsWithCounters() {
        AtomicInteger counter = new AtomicInteger();
        // Source only produces 0,1,2 — drawing a 4th distinct value is impossible.
        Unique<Integer> unique = Unique.of(() -> counter.getAndIncrement() % 3, 50);

        assertThat(unique.get()).isEqualTo(0);
        assertThat(unique.get()).isEqualTo(1);
        assertThat(unique.get()).isEqualTo(2);
        assertThatThrownBy(unique::get)
                .isInstanceOf(UniqueExhaustedException.class)
                .hasMessageContaining("50")
                .hasMessageContaining("3")
                .satisfies(t -> {
                    UniqueExhaustedException ex = (UniqueExhaustedException) t;
                    assertThat(ex.seenCount()).isEqualTo(3);
                    assertThat(ex.retryBudget()).isEqualTo(50);
                });
    }

    @Test
    void retryBudget_isHonoured() {
        AtomicInteger calls = new AtomicInteger();
        Unique<String> unique = Unique.of(() -> {
            calls.incrementAndGet();
            return "same";
        }, 5);

        assertThat(unique.get()).isEqualTo("same");
        assertThat(calls.get()).isEqualTo(1);

        assertThatThrownBy(unique::get).isInstanceOf(UniqueExhaustedException.class);
        // First successful draw used 1 call; the failing draw consumed exactly 5 more.
        assertThat(calls.get()).isEqualTo(6);
    }

    @Test
    void reset_clearsSeenSet() {
        AtomicInteger counter = new AtomicInteger();
        Unique<Integer> unique = Unique.of(() -> counter.getAndIncrement() % 2, 10);

        unique.list(2);
        assertThat(unique.seenCount()).isEqualTo(2);

        unique.reset();
        assertThat(unique.seenCount()).isZero();
        // After reset, can draw the same values again.
        assertThat(unique.list(2)).hasSize(2);
    }

    @Test
    void invalidArguments_throw() {
        assertThatThrownBy(() -> Unique.of(() -> "x", 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Unique.of(null))
                .isInstanceOf(NullPointerException.class);
    }
}

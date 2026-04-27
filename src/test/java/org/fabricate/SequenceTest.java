package org.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class SequenceTest {

    @Test
    void counter_increasesMonotonically() {
        Sequence.Counter c = Sequence.counter();
        assertThat(c.next()).isEqualTo(0L);
        assertThat(c.next()).isEqualTo(1L);
        assertThat(c.next()).isEqualTo(2L);
    }

    @Test
    void counter_canStartFromGivenValue() {
        Sequence.Counter c = Sequence.counter(1000);
        assertThat(c.next()).isEqualTo(1000L);
        assertThat(c.next()).isEqualTo(1001L);
    }

    @Test
    void counter_formatPrependsPrefix() {
        Sequence.Counter c = Sequence.counter("USR-", 1);
        assertThat(c.format()).isEqualTo("USR-1");
        assertThat(c.format()).isEqualTo("USR-2");
    }

    @Test
    void counter_formatWithFunction() {
        Sequence.Counter c = Sequence.counter(0);
        String s = c.format(i -> String.format("ord-%05d", i));
        assertThat(s).isEqualTo("ord-00000");
    }

    @Test
    void cycle_walksValuesAndWraps() {
        Sequence.Cycle<String> roles = Sequence.cycle("admin", "user", "guest");
        assertThat(roles.next()).isEqualTo("admin");
        assertThat(roles.next()).isEqualTo("user");
        assertThat(roles.next()).isEqualTo("guest");
        assertThat(roles.next()).isEqualTo("admin");
    }

    @Test
    void cycle_acceptsList() {
        Sequence.Cycle<Integer> c = Sequence.cycle(List.of(1, 2));
        assertThat(c.next()).isEqualTo(1);
        assertThat(c.next()).isEqualTo(2);
        assertThat(c.next()).isEqualTo(1);
    }

    @Test
    void cycle_emptyThrows() {
        assertThatThrownBy(() -> Sequence.cycle())
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Sequence.cycle(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

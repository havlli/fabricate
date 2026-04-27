package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class DatesTest {

    private static final Clock FIXED = Clock.fixed(Instant.parse("2026-04-27T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void between_isWithinRange() {
        Dates dates = new Dates(Rng.seeded(0L), FIXED);
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2021, 12, 31);

        for (int i = 0; i < 100; i++) {
            LocalDate d = dates.between(start, end);
            assertThat(d).isBetween(start, end);
        }
    }

    @Test
    void between_throwsIfReversed() {
        Dates dates = new Dates(Rng.seeded(1L), FIXED);
        assertThatThrownBy(() -> dates.between(LocalDate.of(2024, 1, 2), LocalDate.of(2024, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void recent_isWithinTheLastNDays() {
        Dates dates = new Dates(Rng.seeded(2L), FIXED);
        LocalDate today = LocalDate.now(FIXED);

        for (int i = 0; i < 50; i++) {
            LocalDate d = dates.recent(30);
            assertThat(d).isBetween(today.minusDays(30), today);
        }
    }

    @Test
    void future_isWithinTheNextNDays() {
        Dates dates = new Dates(Rng.seeded(3L), FIXED);
        LocalDate today = LocalDate.now(FIXED);

        for (int i = 0; i < 50; i++) {
            LocalDate d = dates.future(45);
            assertThat(d).isBetween(today, today.plusDays(45));
        }
    }

    @Test
    void past_isStrictlyInThePast() {
        Dates dates = new Dates(Rng.seeded(4L), FIXED);
        LocalDate today = LocalDate.now(FIXED);

        for (int i = 0; i < 50; i++) {
            LocalDate d = dates.past(5);
            assertThat(d).isBefore(today);
        }
    }

    @Test
    void instantBetween_isWithinRange() {
        Dates dates = new Dates(Rng.seeded(5L), FIXED);
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = Instant.parse("2024-12-31T23:59:59Z");

        for (int i = 0; i < 50; i++) {
            Instant got = dates.instantBetween(start, end);
            assertThat(got).isBetween(start, end);
        }
    }

    @Test
    void recentInstant_isWithinTheLastNMinutes() {
        Dates dates = new Dates(Rng.seeded(6L), FIXED);
        Instant now = Instant.now(FIXED);

        for (int i = 0; i < 50; i++) {
            Instant got = dates.recentInstant(60);
            assertThat(got).isBetween(now.minusSeconds(3600), now);
        }
    }

    @Test
    void localDateTimeBetween_isWithinRange() {
        Dates dates = new Dates(Rng.seeded(7L), FIXED);
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);

        for (int i = 0; i < 50; i++) {
            LocalDateTime got = dates.localDateTimeBetween(start, end);
            assertThat(got).isBetween(start, end);
        }
    }

    @Test
    void weekday_returnsAllSevenOverManyTrials() {
        Dates dates = new Dates(Rng.seeded(8L), FIXED);
        java.util.EnumSet<DayOfWeek> seen = java.util.EnumSet.noneOf(DayOfWeek.class);
        for (int i = 0; i < 200; i++) {
            seen.add(dates.weekday());
        }
        assertThat(seen).hasSize(7);
    }

    @Test
    void month_returnsAllTwelveOverManyTrials() {
        Dates dates = new Dates(Rng.seeded(9L), FIXED);
        java.util.EnumSet<Month> seen = java.util.EnumSet.noneOf(Month.class);
        for (int i = 0; i < 500; i++) {
            seen.add(dates.month());
        }
        assertThat(seen).hasSize(12);
    }

    @Test
    void durationUpTo_isInRange() {
        Dates dates = new Dates(Rng.seeded(10L), FIXED);

        for (int i = 0; i < 50; i++) {
            Duration d = dates.durationUpTo(3600);
            assertThat(d.getSeconds()).isBetween(0L, 3600L);
        }
    }
}

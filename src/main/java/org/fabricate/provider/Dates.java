package org.fabricate.provider;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import org.fabricate.random.Rng;

/**
 * Date and time primitives — relative ranges, instants, weekdays, months.
 *
 * {@snippet :
 * LocalDate d  = fab.dates().between(LocalDate.of(2020,1,1), LocalDate.now());
 * LocalDate r  = fab.dates().recent(30);   // within the last 30 days
 * LocalDate f  = fab.dates().future(60);   // within the next 60 days
 * Instant   i  = fab.dates().instantBetween(Instant.now().minusSeconds(3600), Instant.now());
 * }
 */
public final class Dates {

    private final Rng rng;
    private final Clock clock;

    public Dates(Rng rng) {
        this(rng, Clock.systemDefaultZone());
    }

    public Dates(Rng rng, Clock clock) {
        this.rng = rng;
        this.clock = clock;
    }

    /** A date in {@code [startInclusive, endInclusive]}. */
    public LocalDate between(LocalDate startInclusive, LocalDate endInclusive) {
        if (endInclusive.isBefore(startInclusive)) {
            throw new IllegalArgumentException("endInclusive must be >= startInclusive");
        }
        long start = startInclusive.toEpochDay();
        long end = endInclusive.toEpochDay();
        return LocalDate.ofEpochDay(rng.nextLong(start, end + 1));
    }

    /** A date within the last {@code days} days, including today. */
    public LocalDate recent(int days) {
        if (days < 0) throw new IllegalArgumentException("days must be >= 0");
        LocalDate today = LocalDate.now(clock);
        return between(today.minusDays(days), today);
    }

    /** A date within the next {@code days} days, including today. */
    public LocalDate future(int days) {
        if (days < 0) throw new IllegalArgumentException("days must be >= 0");
        LocalDate today = LocalDate.now(clock);
        return between(today, today.plusDays(days));
    }

    /** A date in the past {@code years} years, exclusive of today. */
    public LocalDate past(int years) {
        if (years <= 0) throw new IllegalArgumentException("years must be > 0");
        LocalDate today = LocalDate.now(clock);
        return between(today.minusYears(years), today.minusDays(1));
    }

    /** A {@link LocalDateTime} in {@code [startInclusive, endInclusive]}. */
    public LocalDateTime localDateTimeBetween(LocalDateTime startInclusive, LocalDateTime endInclusive) {
        if (endInclusive.isBefore(startInclusive)) {
            throw new IllegalArgumentException("endInclusive must be >= startInclusive");
        }
        long start = startInclusive.toEpochSecond(ZoneOffset.UTC);
        long end = endInclusive.toEpochSecond(ZoneOffset.UTC);
        long sec = rng.nextLong(start, end + 1);
        return LocalDateTime.ofEpochSecond(sec, 0, ZoneOffset.UTC);
    }

    /** An {@link Instant} in {@code [startInclusive, endInclusive]}. */
    public Instant instantBetween(Instant startInclusive, Instant endInclusive) {
        if (endInclusive.isBefore(startInclusive)) {
            throw new IllegalArgumentException("endInclusive must be >= startInclusive");
        }
        long start = startInclusive.getEpochSecond();
        long end = endInclusive.getEpochSecond();
        return Instant.ofEpochSecond(rng.nextLong(start, end + 1));
    }

    /** An {@link Instant} within the last {@code minutes} minutes. */
    public Instant recentInstant(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("minutes must be >= 0");
        Instant now = Instant.now(clock);
        return instantBetween(now.minusSeconds(60L * minutes), now);
    }

    /** A random day of the week. */
    public DayOfWeek weekday() {
        return DayOfWeek.of(1 + rng.nextInt(7));
    }

    /** A random month. */
    public Month month() {
        return Month.of(1 + rng.nextInt(12));
    }

    /** A random duration up to (and including) {@code maxSeconds}. */
    public Duration durationUpTo(long maxSeconds) {
        if (maxSeconds < 0) throw new IllegalArgumentException("maxSeconds must be >= 0");
        return Duration.ofSeconds(rng.nextLong(0, maxSeconds + 1));
    }

    /** A weekday (Mon–Fri) date in {@code [startInclusive, endInclusive]}. */
    public LocalDate workingDayBetween(LocalDate startInclusive, LocalDate endInclusive) {
        if (endInclusive.isBefore(startInclusive)) {
            throw new IllegalArgumentException("endInclusive must be >= startInclusive");
        }
        if (!hasAny(startInclusive, endInclusive, false)) {
            throw new IllegalArgumentException("range contains no weekdays");
        }
        for (int i = 0; i < 32; i++) {
            LocalDate d = between(startInclusive, endInclusive);
            if (!isWeekend(d)) return d;
        }
        return scanForward(startInclusive, endInclusive, false);
    }

    /** A weekend (Sat/Sun) date in {@code [startInclusive, endInclusive]}. */
    public LocalDate weekendDayBetween(LocalDate startInclusive, LocalDate endInclusive) {
        if (endInclusive.isBefore(startInclusive)) {
            throw new IllegalArgumentException("endInclusive must be >= startInclusive");
        }
        if (!hasAny(startInclusive, endInclusive, true)) {
            throw new IllegalArgumentException("range contains no weekends");
        }
        for (int i = 0; i < 32; i++) {
            LocalDate d = between(startInclusive, endInclusive);
            if (isWeekend(d)) return d;
        }
        return scanForward(startInclusive, endInclusive, true);
    }

    /** A weekday (Mon–Fri) within the next {@code days} days, including today if it qualifies. */
    public LocalDate workingDayInNext(int days) {
        if (days < 0) throw new IllegalArgumentException("days must be >= 0");
        LocalDate today = LocalDate.now(clock);
        return workingDayBetween(today, today.plusDays(days));
    }

    /** True if {@code date} is Saturday or Sunday. */
    public boolean isWeekend(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }

    private boolean hasAny(LocalDate start, LocalDate end, boolean weekend) {
        long days = end.toEpochDay() - start.toEpochDay() + 1;
        long limit = Math.min(days, 14);
        for (long i = 0; i < limit; i++) {
            LocalDate d = start.plusDays(i);
            if (isWeekend(d) == weekend) return true;
        }
        return false;
    }

    private LocalDate scanForward(LocalDate start, LocalDate end, boolean weekend) {
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            if (isWeekend(d) == weekend) return d;
        }
        throw new IllegalStateException("no matching day found");
    }
}

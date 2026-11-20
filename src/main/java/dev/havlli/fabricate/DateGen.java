package dev.havlli.fabricate;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

/**
 * A source of date values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface DateGen {

    /** A date in {@code [startInclusive, endInclusive]}. */
    LocalDate between(LocalDate startInclusive, LocalDate endInclusive);

    /** A date within the last {@code days} days, including today. */
    LocalDate recent(int days);

    /** A date within the next {@code days} days, including today. */
    LocalDate future(int days);

    /** A date in the past {@code years} years, exclusive of today. */
    LocalDate past(int years);

    /** A {@link LocalDateTime} in {@code [startInclusive, endInclusive]}. */
    LocalDateTime localDateTimeBetween(LocalDateTime startInclusive, LocalDateTime endInclusive);

    /** An {@link Instant} in {@code [startInclusive, endInclusive]}. */
    Instant instantBetween(Instant startInclusive, Instant endInclusive);

    /** An {@link Instant} within the last {@code minutes} minutes. */
    Instant recentInstant(int minutes);

    /** A random day of the week. */
    DayOfWeek weekday();

    /** A random month. */
    Month month();

    /** A random duration up to (and including) {@code maxSeconds}. */
    Duration durationUpTo(long maxSeconds);

    /** A weekday (Mon–Fri) date in {@code [startInclusive, endInclusive]}. */
    LocalDate workingDayBetween(LocalDate startInclusive, LocalDate endInclusive);

    /** A weekend (Sat/Sun) date in {@code [startInclusive, endInclusive]}. */
    LocalDate weekendDayBetween(LocalDate startInclusive, LocalDate endInclusive);

    /** A weekday (Mon–Fri) within the next {@code days} days, including today if it qualifies. */
    LocalDate workingDayInNext(int days);

    /** True if {@code date} is Saturday or Sunday. */
    boolean isWeekend(LocalDate date);
}

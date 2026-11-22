package dev.havlli.fabricate;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import dev.havlli.fabricate.provider.Dates;

final class DefaultDateGen implements DateGen {

    private final Dates provider;

    DefaultDateGen(Fabricate fab) {
        this.provider = fab.dates;
    }

    @Override
    public LocalDate between(LocalDate startInclusive, LocalDate endInclusive) {
        return provider.between(startInclusive, endInclusive);
    }

    @Override
    public LocalDate recent(int days) {
        return provider.recent(days);
    }

    @Override
    public LocalDate future(int days) {
        return provider.future(days);
    }

    @Override
    public LocalDate past(int years) {
        return provider.past(years);
    }

    @Override
    public LocalDateTime localDateTimeBetween(LocalDateTime startInclusive, LocalDateTime endInclusive) {
        return provider.localDateTimeBetween(startInclusive, endInclusive);
    }

    @Override
    public Instant instantBetween(Instant startInclusive, Instant endInclusive) {
        return provider.instantBetween(startInclusive, endInclusive);
    }

    @Override
    public Instant recentInstant(int minutes) {
        return provider.recentInstant(minutes);
    }

    @Override
    public DayOfWeek weekday() {
        return provider.weekday();
    }

    @Override
    public Month month() {
        return provider.month();
    }

    @Override
    public Duration durationUpTo(long maxSeconds) {
        return provider.durationUpTo(maxSeconds);
    }

    @Override
    public LocalDate workingDayBetween(LocalDate startInclusive, LocalDate endInclusive) {
        return provider.workingDayBetween(startInclusive, endInclusive);
    }

    @Override
    public LocalDate weekendDayBetween(LocalDate startInclusive, LocalDate endInclusive) {
        return provider.weekendDayBetween(startInclusive, endInclusive);
    }

    @Override
    public LocalDate workingDayInNext(int days) {
        return provider.workingDayInNext(days);
    }

    @Override
    public boolean isWeekend(LocalDate date) {
        return provider.isWeekend(date);
    }
}

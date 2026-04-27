package org.fabricate.provider;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Stream;
import org.fabricate.random.Rng;

public final class DatesOfBirth {

    private final Rng rng;
    private final Clock clock;

    public DatesOfBirth(Rng rng) {
        this(rng, Clock.systemDefaultZone());
    }

    public DatesOfBirth(Rng rng, Clock clock) {
        this.rng = rng;
        this.clock = clock;
    }

    /** Birthdate for a person aged in {@code [18, 90)} on the clock's "today". */
    public LocalDate birthdate() {
        return birthdate(18, 90);
    }

    /**
     * Birthdate for a person aged in {@code [minAge, maxAgeExclusive)} today.
     *
     * @throws IllegalArgumentException if {@code minAge < 0} or
     *         {@code maxAgeExclusive <= minAge}.
     */
    public LocalDate birthdate(int minAge, int maxAgeExclusive) {
        if (minAge < 0) {
            throw new IllegalArgumentException("minAge must be >= 0, was " + minAge);
        }
        if (maxAgeExclusive <= minAge) {
            throw new IllegalArgumentException(
                    "maxAgeExclusive must be > minAge, was " + maxAgeExclusive + " <= " + minAge);
        }
        LocalDate today = LocalDate.now(clock);
        LocalDate latest = today.minus(Period.ofYears(minAge));
        LocalDate earliest = today.minus(Period.ofYears(maxAgeExclusive));
        long days = earliest.toEpochDay()
                + rng.nextLong(0, latest.toEpochDay() - earliest.toEpochDay());
        return LocalDate.ofEpochDay(days);
    }

    /** Infinite stream of birthdates using the default age range. Not parallel-safe. */
    public Stream<LocalDate> stream() {
        return Stream.generate(this::birthdate);
    }

    /** Eagerly produces {@code count} birthdates using the default age range. */
    public List<LocalDate> list(int count) {
        return stream().limit(count).toList();
    }
}

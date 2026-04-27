package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class DatesOfBirthTest {

    private static final Clock FIXED = Clock.fixed(Instant.parse("2026-04-27T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void birthdate_default_isInValidAdultRange() {
        DatesOfBirth dob = new DatesOfBirth(Rng.seeded(0L), FIXED);

        for (int i = 0; i < 100; i++) {
            LocalDate date = dob.birthdate();
            int age = Period.between(date, LocalDate.now(FIXED)).getYears();
            assertThat(age).isBetween(18, 89);
        }
    }

    @Test
    void birthdate_customRange_isHonoured() {
        DatesOfBirth dob = new DatesOfBirth(Rng.seeded(0L), FIXED);

        for (int i = 0; i < 100; i++) {
            LocalDate date = dob.birthdate(25, 30);
            int age = Period.between(date, LocalDate.now(FIXED)).getYears();
            assertThat(age).isBetween(25, 29);
        }
    }

    @Test
    void birthdate_invalidBounds_throw() {
        DatesOfBirth dob = new DatesOfBirth(Rng.seeded(0L));

        assertThatThrownBy(() -> dob.birthdate(-1, 5))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> dob.birthdate(30, 30))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> dob.birthdate(30, 20))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

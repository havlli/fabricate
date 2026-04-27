package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.fabricate.locale.ChineseLocale;
import org.fabricate.locale.EnglishLocale;
import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class NamesTest {

    @Test
    void firstName_drawsFromLocalePool() {
        Names names = new Names(EnglishLocale.INSTANCE, Rng.seeded(0L));

        for (int i = 0; i < 100; i++) {
            assertThat(EnglishLocale.INSTANCE.firstNames()).contains(names.firstName());
        }
    }

    @Test
    void lastName_drawsFromLocalePool() {
        Names names = new Names(EnglishLocale.INSTANCE, Rng.seeded(0L));

        for (int i = 0; i < 100; i++) {
            assertThat(EnglishLocale.INSTANCE.lastNames()).contains(names.lastName());
        }
    }

    @Test
    void lastName_canDrawFromTailOfPool() {
        // Regression test for a former off-by-array bug where lastName indexing
        // was bounded by firstNames.length, masking the tail of the lastName pool.
        // We seed an Rng and ensure we observe the very last entry within a
        // reasonable sample size.
        String tailEntry = EnglishLocale.INSTANCE.lastNames()
                .get(EnglishLocale.INSTANCE.lastNames().size() - 1);

        Names names = new Names(EnglishLocale.INSTANCE, Rng.seeded(0L));
        boolean sawTail = false;
        for (int i = 0; i < 1000 && !sawTail; i++) {
            sawTail = names.lastName().equals(tailEntry);
        }
        assertThat(sawTail)
                .as("last entry of lastNames pool should be reachable")
                .isTrue();
    }

    @Test
    void fullName_usesEnglishDelimiter() {
        Names names = new Names(EnglishLocale.INSTANCE, Rng.seeded(0L));

        assertThat(names.fullName()).contains(" ");
    }

    @Test
    void fullName_usesEmptyChineseDelimiter() {
        Names names = new Names(ChineseLocale.INSTANCE, Rng.seeded(0L));

        String full = names.fullName();
        assertThat(full)
                .doesNotContain(" ")
                .satisfies(s -> assertThat(s.codePointCount(0, s.length())).isGreaterThan(1));
        assertThat(ChineseLocale.INSTANCE.locale()).isEqualTo(Locale.CHINESE);
    }
}

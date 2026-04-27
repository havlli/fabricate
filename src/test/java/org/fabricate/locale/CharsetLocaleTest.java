package org.fabricate.locale;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.Character.UnicodeBlock;
import java.util.Locale;
import java.util.Set;
import org.fabricate.Fabricate;
import org.junit.jupiter.api.Test;

class CharsetLocaleTest {

    @Test
    void russianLocale_namesAreCyrillic() {
        Fabricate fab = Fabricate.builder().locale(Locale.of("ru")).seed(0L).build();

        for (int i = 0; i < 30; i++) {
            assertThat(allCharsIn(fab.names().firstName(), Set.of(UnicodeBlock.CYRILLIC))).isTrue();
            assertThat(allCharsIn(fab.names().lastName(), Set.of(UnicodeBlock.CYRILLIC))).isTrue();
        }
    }

    @Test
    void japaneseLocale_namesAreCjkOrKana() {
        Fabricate fab = Fabricate.builder().locale(Locale.JAPANESE).seed(0L).build();
        Set<UnicodeBlock> blocks = Set.of(
                UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS,
                UnicodeBlock.HIRAGANA,
                UnicodeBlock.KATAKANA);

        for (int i = 0; i < 30; i++) {
            assertThat(allCharsIn(fab.names().firstName(), blocks)).isTrue();
            assertThat(allCharsIn(fab.names().lastName(), blocks)).isTrue();
        }
    }

    @Test
    void koreanLocale_namesAreHangul() {
        Fabricate fab = Fabricate.builder().locale(Locale.KOREAN).seed(0L).build();
        Set<UnicodeBlock> blocks = Set.of(
                UnicodeBlock.HANGUL_SYLLABLES,
                UnicodeBlock.HANGUL_JAMO,
                UnicodeBlock.HANGUL_COMPATIBILITY_JAMO);

        for (int i = 0; i < 30; i++) {
            assertThat(allCharsIn(fab.names().firstName(), blocks)).isTrue();
            assertThat(allCharsIn(fab.names().lastName(), blocks)).isTrue();
        }
    }

    @Test
    void arabicLocale_namesAreArabicScript() {
        Fabricate fab = Fabricate.builder().locale(Locale.of("ar")).seed(0L).build();

        for (int i = 0; i < 30; i++) {
            assertThat(allCharsIn(fab.names().firstName(), Set.of(UnicodeBlock.ARABIC))).isTrue();
            assertThat(allCharsIn(fab.names().lastName(), Set.of(UnicodeBlock.ARABIC))).isTrue();
        }
    }

    @Test
    void germanLocale_emailHasNoUmlauts() {
        Fabricate fab = Fabricate.builder().locale(Locale.GERMAN).seed(0L).build();

        for (int i = 0; i < 50; i++) {
            String email = fab.emails().email();
            assertThat(email).doesNotContainAnyWhitespaces();
            assertThat(email).matches("[^@]+@[^@]+");
            String local = email.substring(0, email.indexOf('@'));
            assertThat(local).doesNotContain("ä", "ö", "ü", "ß", "Ä", "Ö", "Ü");
        }
    }

    @Test
    void germanLocale_namesUseLatinScript() {
        Fabricate fab = Fabricate.builder().locale(Locale.GERMAN).seed(0L).build();

        Set<UnicodeBlock> blocks = Set.of(
                UnicodeBlock.BASIC_LATIN, UnicodeBlock.LATIN_1_SUPPLEMENT, UnicodeBlock.LATIN_EXTENDED_A);
        for (int i = 0; i < 30; i++) {
            assertThat(allCharsIn(fab.names().firstName(), blocks)).isTrue();
            assertThat(allCharsIn(fab.names().lastName(), blocks)).isTrue();
        }
    }

    @Test
    void availableLocalesIncludesAllCharsetLocales() {
        Set<Locale> available = Fabricate.availableLocales();
        assertThat(available).contains(
                Locale.ENGLISH, Locale.CHINESE, Locale.JAPANESE, Locale.KOREAN, Locale.GERMAN,
                Locale.of("ru"), Locale.of("ar"));
    }

    private static boolean allCharsIn(String s, Set<UnicodeBlock> blocks) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ' || c == '-' || c == '\'') continue;
            UnicodeBlock b = UnicodeBlock.of(c);
            if (!blocks.contains(b)) {
                return false;
            }
        }
        return true;
    }
}

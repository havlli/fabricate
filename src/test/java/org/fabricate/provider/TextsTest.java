package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class TextsTest {

    @Test
    void word_drawsFromLoremPool() {
        Texts texts = new Texts(Rng.seeded(0L));

        for (int i = 0; i < 50; i++) {
            assertThat(Texts.wordPool()).contains(texts.word());
        }
    }

    @Test
    void words_returnsRequestedCount() {
        Texts texts = new Texts(Rng.seeded(0L));

        String w = texts.words(7);

        assertThat(w.split(" ")).hasSize(7);
    }

    @Test
    void sentence_isCapitalizedAndEndsWithPeriod() {
        Texts texts = new Texts(Rng.seeded(0L));

        String s = texts.sentence();

        assertThat(s).endsWith(".");
        assertThat(Character.isUpperCase(s.charAt(0))).isTrue();
        assertThat(s.split(" ").length).isBetween(6, 14);
    }

    @Test
    void paragraphs_areSeparatedByBlankLines() {
        Texts texts = new Texts(Rng.seeded(0L));

        String body = texts.paragraphs(3);

        assertThat(body.split("\n\n")).hasSize(3);
    }

    @Test
    void slug_isLowercaseHyphenated() {
        Texts texts = new Texts(Rng.seeded(0L));

        String slug = texts.slug();

        assertThat(slug).matches("[a-z]+(-[a-z]+){1,3}");
    }

    @Test
    void alphanumeric_matchesCharsetAndLength() {
        Texts texts = new Texts(Rng.seeded(0L));

        String token = texts.alphanumeric(20);

        assertThat(token).hasSize(20).matches("[A-Za-z0-9]+");
    }

    @Test
    void hex_matchesCharsetAndLength() {
        Texts texts = new Texts(Rng.seeded(0L));

        String h = texts.hex(16);

        assertThat(h).hasSize(16).matches("[0-9a-f]+");
    }

    @Test
    void aboutLength_returnsRoughlyTheRequestedLength() {
        Texts texts = new Texts(Rng.seeded(0L));

        String s = texts.aboutLength(50);

        assertThat(s.length()).isBetween(25, 100);
    }

    @Test
    void invalidArgs_throw() {
        Texts texts = new Texts(Rng.seeded(0L));

        assertThatThrownBy(() -> texts.words(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> texts.paragraphs(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> texts.alphanumeric(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> texts.hex(0)).isInstanceOf(IllegalArgumentException.class);
    }
}

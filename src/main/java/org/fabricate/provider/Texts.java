package org.fabricate.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.fabricate.random.Rng;

/**
 * Text fixtures: lorem ipsum prose, slugs, and short tokens.
 *
 * {@snippet :
 * String headline = fab.texts().sentence();
 * String body     = fab.texts().paragraphs(3);
 * String slug     = fab.texts().slug();              // "ut-iure-debitis"
 * String hex8     = fab.texts().hex(8);              // "9f3a0c41"
 * String code     = fab.texts().alphanumeric(10);    // "k4Hb9dQ3xV"
 * }
 */
public final class Texts {

    private static final List<String> LOREM_WORDS = List.of(
            "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit",
            "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore",
            "magna", "aliqua", "enim", "ad", "minim", "veniam", "quis", "nostrud",
            "exercitation", "ullamco", "laboris", "nisi", "aliquip", "ex", "ea", "commodo",
            "consequat", "duis", "aute", "irure", "in", "reprehenderit", "voluptate",
            "velit", "esse", "cillum", "fugiat", "nulla", "pariatur", "excepteur", "sint",
            "occaecat", "cupidatat", "non", "proident", "sunt", "culpa", "qui", "officia",
            "deserunt", "mollit", "anim", "id", "est", "laborum", "perspiciatis", "unde",
            "omnis", "iste", "natus", "error", "voluptatem", "accusantium", "doloremque",
            "laudantium", "totam", "rem", "aperiam", "eaque", "ipsa", "quae", "ab", "illo",
            "inventore", "veritatis", "quasi", "architecto", "beatae", "vitae", "dicta",
            "explicabo", "nemo", "ipsam", "quia", "voluptas", "aspernatur", "aut", "odit",
            "fugit", "sed", "consequuntur", "magni", "dolores", "ratione", "sequi",
            "nesciunt", "neque", "porro", "quisquam", "dolorem", "adipisci", "numquam",
            "eius", "modi", "tempora", "incidunt", "magnam", "quaerat", "minima", "nostrum",
            "corporis", "suscipit", "laboriosam", "aliquid", "commodi", "iure", "vel",
            "eum", "iusto", "odio", "dignissimos", "ducimus", "blanditiis", "praesentium",
            "voluptatum", "deleniti", "atque", "corrupti", "quos", "quas", "molestias",
            "soluta", "nobis", "eligendi", "optio", "cumque", "nihil", "impedit", "quo",
            "minus", "maxime", "placeat", "facere", "possimus", "assumenda", "repellendus",
            "temporibus", "autem", "quibusdam", "officiis", "debitis", "rerum", "saepe",
            "eveniet", "voluptates", "repudiandae", "recusandae", "itaque", "earum",
            "hic", "tenetur", "sapiente", "delectus", "reiciendis", "voluptatibus",
            "maiores", "alias", "perferendis", "doloribus", "asperiores", "repellat"
    );

    private static final String ALPHANUMERIC =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String HEX = "0123456789abcdef";

    private final Rng rng;
    private final Numbers numbers;

    public Texts(Rng rng) {
        this.rng = rng;
        this.numbers = new Numbers(rng);
    }

    /** A single lorem-ipsum word. */
    public String word() {
        return rng.pick(LOREM_WORDS);
    }

    /** {@code count} lorem-ipsum words joined by spaces. */
    public String words(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("count must be >= 1, was " + count);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(' ');
            sb.append(word());
        }
        return sb.toString();
    }

    /** A capitalized lorem-ipsum sentence with a period, 6–14 words long. */
    public String sentence() {
        int len = numbers.intBetween(6, 14);
        StringBuilder sb = new StringBuilder(words(len));
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.append('.');
        return sb.toString();
    }

    /** A paragraph of 3–6 sentences. */
    public String paragraph() {
        int sentences = numbers.intBetween(3, 6);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sentences; i++) {
            if (i > 0) sb.append(' ');
            sb.append(sentence());
        }
        return sb.toString();
    }

    /** {@code count} paragraphs joined with blank lines. */
    public String paragraphs(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("count must be >= 1, was " + count);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append("\n\n");
            sb.append(paragraph());
        }
        return sb.toString();
    }

    /** URL-friendly slug of 2–4 words separated by hyphens. */
    public String slug() {
        int len = numbers.intBetween(2, 4);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i > 0) sb.append('-');
            sb.append(word().toLowerCase(Locale.ROOT));
        }
        return sb.toString();
    }

    /** Random alphanumeric token of the given length. */
    public String alphanumeric(int length) {
        return randomChars(length, ALPHANUMERIC);
    }

    /** Random lowercase hex string of the given length. */
    public String hex(int length) {
        return randomChars(length, HEX);
    }

    /**
     * Random text whose length is roughly {@code averageLength} characters,
     * useful for Bean Validation tests. Length varies in {@code [averageLength/2, averageLength*2]}.
     */
    public String aboutLength(int averageLength) {
        int len = numbers.intBetween(Math.max(1, averageLength / 2), Math.max(1, averageLength * 2));
        StringBuilder sb = new StringBuilder(len);
        while (sb.length() < len) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(word());
        }
        return sb.substring(0, len);
    }

    private String randomChars(int length, String alphabet) {
        if (length < 1) {
            throw new IllegalArgumentException("length must be >= 1, was " + length);
        }
        char[] out = new char[length];
        for (int i = 0; i < length; i++) {
            out[i] = alphabet.charAt(numbers.intBetween(0, alphabet.length() - 1));
        }
        return new String(out);
    }

    /** Returns the underlying word pool — useful for tests verifying coverage. */
    static List<String> wordPool() {
        return new ArrayList<>(LOREM_WORDS);
    }
}

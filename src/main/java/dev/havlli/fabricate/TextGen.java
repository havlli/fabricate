package dev.havlli.fabricate;

/**
 * A source of text values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface TextGen {

    /** A single lorem-ipsum word. */
    String word();

    /** {@code count} lorem-ipsum words joined by spaces. */
    String words(int count);

    /** A capitalized lorem-ipsum sentence with a period, 6–14 words long. */
    String sentence();

    /** A paragraph of 3–6 sentences. */
    String paragraph();

    /** {@code count} paragraphs joined with blank lines. */
    String paragraphs(int count);

    /** URL-friendly slug of 2–4 words separated by hyphens. */
    String slug();

    /** Random alphanumeric token of the given length. */
    String alphanumeric(int length);

    /** Random lowercase hex string of the given length. */
    String hex(int length);

    /**
     * Random text whose length is roughly {@code averageLength} characters,
     * useful for Bean Validation tests. Length varies in {@code [averageLength/2, averageLength*2]}.
     */
    String aboutLength(int averageLength);
}

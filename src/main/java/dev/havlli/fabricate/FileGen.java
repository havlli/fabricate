package dev.havlli.fabricate;

/**
 * A source of file values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface FileGen {

    /** A random file extension without leading dot. */
    String extension();

    /** A slug-based file name with a random extension. */
    String name();

    /** A slug-based file name with the given extension (with or without leading dot). */
    String name(String extension);

    /** Unix-style absolute path with 2–4 directory levels and a random file at the leaf. */
    String path();

    /** A directory path without a trailing file. */
    String directory();

    /** A common MIME type drawn from a curated list. */
    String mimeType();

    /** The MIME type matching a given extension, or {@code application/octet-stream} if unknown. */
    String mimeTypeFor(String extension);

    /** A plausible byte size for a file: log-uniform between 1KB and 10MB. */
    long sizeBytes();
}

package dev.havlli.fabricate;

/**
 * A source of cryptographically strong passwords, independent of the Fabricate seed.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface PasswordGen {

    /** Draws a 16-character password using the default alphabet, independently of the Fabricate seed. */
    String get();

    /** Draws a password of positive {@code length} using the default alphabet. */
    String get(int length);

    /** Draws a password of positive {@code length} from the nonempty {@code alphabet}. */
    String get(int length, String alphabet);
}

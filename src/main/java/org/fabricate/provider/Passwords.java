package org.fabricate.provider;

import java.security.SecureRandom;

/**
 * Cryptographically strong password generator.
 *
 * Intentionally backed by {@link SecureRandom} rather than the configured
 * {@link org.fabricate.random.Rng}: passwords destined for fixtures that
 * may be persisted should not be predictable from a public test seed.
 */
public final class Passwords {

    private static final String DEFAULT_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
    private static final int DEFAULT_LENGTH = 16;

    private final SecureRandom secureRandom = new SecureRandom();

    public String password() {
        return password(DEFAULT_LENGTH, DEFAULT_ALPHABET);
    }

    public String password(int length) {
        return password(length, DEFAULT_ALPHABET);
    }

    public String password(int length, String alphabet) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be > 0, was " + length);
        }
        if (alphabet.isEmpty()) {
            throw new IllegalArgumentException("alphabet must not be empty");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(secureRandom.nextInt(alphabet.length())));
        }
        return sb.toString();
    }
}

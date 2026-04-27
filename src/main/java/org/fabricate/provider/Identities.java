package org.fabricate.provider;

import java.util.UUID;
import org.fabricate.random.Rng;

public final class Identities {

    private final Rng rng;

    public Identities(Rng rng) {
        this.rng = rng;
    }

    /**
     * Deterministic UUID derived from the current RNG state.
     *
     * Unlike {@link UUID#randomUUID()}, this respects the configured seed
     * so reproducible test runs produce reproducible UUIDs.
     */
    public UUID uuid() {
        long mostSig = rng.delegate().nextLong();
        long leastSig = rng.delegate().nextLong();
        // RFC 4122 v4 (random) with variant 10
        mostSig &= 0xFFFFFFFFFFFF0FFFL;
        mostSig |= 0x0000000000004000L;
        leastSig &= 0x3FFFFFFFFFFFFFFFL;
        leastSig |= 0x8000000000000000L;
        return new UUID(mostSig, leastSig);
    }

    public String username(String firstName, String lastName, UUID uuid) {
        if (lastName.isEmpty()) {
            throw new IllegalArgumentException("lastName must not be empty");
        }
        String head = uuid.toString().substring(0, 8);
        return firstName.toLowerCase() + lastName.toLowerCase().charAt(0) + head;
    }
}

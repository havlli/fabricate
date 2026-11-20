package dev.havlli.fabricate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * A source of identity values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface IdentityGen {

    /**
     * Deterministic UUID derived from the current RNG state.
     *
     * Unlike {@link UUID#randomUUID()}, this respects the configured seed
     * so reproducible test runs produce reproducible UUIDs.
     */
    UUID uuid();

    /** Derives a username from the given names and UUID. The last name must not be empty. */
    String username(String firstName, String lastName, UUID uuid);

    /** Infinite stream of UUIDs. Use {@code .limit(n)} to bound. Not parallel-safe. */
    Stream<UUID> stream();

    /** Eagerly produces {@code count} UUIDs. */
    List<UUID> list(int count);
}

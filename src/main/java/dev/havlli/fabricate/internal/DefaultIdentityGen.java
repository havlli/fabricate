package dev.havlli.fabricate.internal;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import dev.havlli.fabricate.IdentityGen;
import dev.havlli.fabricate.internal.provider.Identities;

public final class DefaultIdentityGen implements IdentityGen {

    private final Identities provider;

    public DefaultIdentityGen(Identities provider) {
        this.provider = provider;
    }

    @Override
    public UUID uuid() {
        return provider.uuid();
    }

    @Override
    public String username(String firstName, String lastName, UUID uuid) {
        return provider.username(firstName, lastName, uuid);
    }

    @Override
    public Stream<UUID> stream() {
        return provider.stream();
    }

    @Override
    public List<UUID> list(int count) {
        return provider.list(count);
    }
}

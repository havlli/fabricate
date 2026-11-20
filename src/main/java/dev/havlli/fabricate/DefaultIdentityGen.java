package dev.havlli.fabricate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import dev.havlli.fabricate.provider.Identities;

final class DefaultIdentityGen implements IdentityGen {

    private final Identities provider;

    DefaultIdentityGen(Fabricate fab) {
        this.provider = fab.identities;
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

package dev.havlli.fabricate.internal;

import dev.havlli.fabricate.PasswordGen;
import dev.havlli.fabricate.internal.provider.Passwords;

public final class DefaultPasswordGen implements PasswordGen {

    private final Passwords provider;

    public DefaultPasswordGen(Passwords provider) {
        this.provider = provider;
    }

    @Override
    public String get() {
        return provider.password();
    }

    @Override
    public String get(int length) {
        return provider.password(length);
    }

    @Override
    public String get(int length, String alphabet) {
        return provider.password(length, alphabet);
    }
}

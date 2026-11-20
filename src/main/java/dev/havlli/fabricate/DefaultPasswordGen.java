package dev.havlli.fabricate;
import dev.havlli.fabricate.provider.Passwords;

final class DefaultPasswordGen implements PasswordGen {

    private final Passwords provider;

    DefaultPasswordGen(Fabricate fab) {
        this.provider = fab.passwords;
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

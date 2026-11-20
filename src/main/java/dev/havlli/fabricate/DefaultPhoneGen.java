package dev.havlli.fabricate;

import java.util.List;
import java.util.stream.Stream;
import dev.havlli.fabricate.provider.Phones;

final class DefaultPhoneGen implements PhoneGen {

    private final Phones provider;

    DefaultPhoneGen(Fabricate fab) {
        this.provider = fab.phones;
    }

    @Override
    public String number() {
        return provider.phoneNumber();
    }

    @Override
    public Stream<String> stream() {
        return provider.stream();
    }

    @Override
    public List<String> list(int count) {
        return provider.list(count);
    }
}

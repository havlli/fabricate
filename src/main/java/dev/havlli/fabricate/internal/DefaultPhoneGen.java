package dev.havlli.fabricate.internal;

import java.util.List;
import java.util.stream.Stream;
import dev.havlli.fabricate.PhoneGen;
import dev.havlli.fabricate.internal.provider.Phones;

public final class DefaultPhoneGen implements PhoneGen {

    private final Phones provider;

    public DefaultPhoneGen(Phones provider) {
        this.provider = provider;
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

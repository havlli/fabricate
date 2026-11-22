package dev.havlli.fabricate.internal;

import java.util.List;
import java.util.stream.Stream;
import dev.havlli.fabricate.NameGen;
import dev.havlli.fabricate.internal.provider.Names;

public final class DefaultNameGen implements NameGen {

    private final Names provider;

    public DefaultNameGen(Names provider) {
        this.provider = provider;
    }

    @Override
    public String first() {
        return provider.firstName();
    }

    @Override
    public String last() {
        return provider.lastName();
    }

    @Override
    public String full() {
        return provider.fullName();
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

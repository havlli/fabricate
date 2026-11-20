package dev.havlli.fabricate;

import java.util.List;
import java.util.stream.Stream;
import dev.havlli.fabricate.provider.Names;

final class DefaultNameGen implements NameGen {

    private final Names provider;

    DefaultNameGen(Fabricate fab) {
        this.provider = fab.names;
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

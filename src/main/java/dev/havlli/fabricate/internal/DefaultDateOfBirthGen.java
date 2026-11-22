package dev.havlli.fabricate.internal;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import dev.havlli.fabricate.DateOfBirthGen;
import dev.havlli.fabricate.internal.provider.DatesOfBirth;

public final class DefaultDateOfBirthGen implements DateOfBirthGen {

    private final DatesOfBirth provider;

    public DefaultDateOfBirthGen(DatesOfBirth provider) {
        this.provider = provider;
    }

    @Override
    public LocalDate get() {
        return provider.birthdate();
    }

    @Override
    public LocalDate get(int minAge, int maxAgeExclusive) {
        return provider.birthdate(minAge, maxAgeExclusive);
    }

    @Override
    public Stream<LocalDate> stream() {
        return provider.stream();
    }

    @Override
    public List<LocalDate> list(int count) {
        return provider.list(count);
    }
}

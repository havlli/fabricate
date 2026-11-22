package dev.havlli.fabricate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import dev.havlli.fabricate.provider.DatesOfBirth;

final class DefaultDateOfBirthGen implements DateOfBirthGen {

    private final DatesOfBirth provider;

    DefaultDateOfBirthGen(Fabricate fab) {
        this.provider = fab.datesOfBirth;
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

package org.fabricate.provider;

import java.util.List;
import java.util.stream.Stream;
import org.fabricate.random.Rng;
import org.fabricate.spi.LocaleData;

public final class Names {

    private final LocaleData locale;
    private final Rng rng;

    public Names(LocaleData locale, Rng rng) {
        this.locale = locale;
        this.rng = rng;
    }

    public String firstName() {
        return rng.pick(locale.firstNames());
    }

    public String lastName() {
        return rng.pick(locale.lastNames());
    }

    public String fullName() {
        return firstName() + locale.nameDelimiter() + lastName();
    }

    /** Infinite stream of full names. Use {@code .limit(n)} to bound. Not parallel-safe. */
    public Stream<String> stream() {
        return Stream.generate(this::fullName);
    }

    /** Eagerly produces {@code count} full names. */
    public List<String> list(int count) {
        return stream().limit(count).toList();
    }
}

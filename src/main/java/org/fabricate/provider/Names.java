package org.fabricate.provider;

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
}

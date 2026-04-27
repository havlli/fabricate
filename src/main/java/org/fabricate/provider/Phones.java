package org.fabricate.provider;

import org.fabricate.random.Rng;
import org.fabricate.spi.LocaleData;

public final class Phones {

    private static final int SUBSCRIBER_DIGITS = 9;

    private final LocaleData locale;
    private final Rng rng;

    public Phones(LocaleData locale, Rng rng) {
        this.locale = locale;
        this.rng = rng;
    }

    public String phoneNumber() {
        StringBuilder sb = new StringBuilder(rng.pick(locale.phoneCountryCodes()));
        for (int i = 0; i < SUBSCRIBER_DIGITS; i++) {
            sb.append(rng.nextInt(10));
        }
        return sb.toString();
    }
}

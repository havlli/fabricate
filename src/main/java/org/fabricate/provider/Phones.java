package org.fabricate.provider;

import java.util.List;
import java.util.stream.Stream;
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

    /** Infinite stream of phone numbers. Use {@code .limit(n)} to bound. Not parallel-safe. */
    public Stream<String> stream() {
        return Stream.generate(this::phoneNumber);
    }

    /** Eagerly produces {@code count} phone numbers. */
    public List<String> list(int count) {
        return stream().limit(count).toList();
    }
}

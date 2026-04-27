package org.fabricate.provider;

import java.util.List;
import java.util.stream.Stream;
import org.fabricate.random.Rng;
import org.fabricate.spi.LocaleData;

public final class Emails {

    private final LocaleData locale;
    private final Rng rng;

    public Emails(LocaleData locale, Rng rng) {
        this.locale = locale;
        this.rng = rng;
    }

    public String email() {
        return email(rng.pick(locale.firstNames()), rng.pick(locale.lastNames()));
    }

    public String email(String firstName, String lastName) {
        String localPart = locale.emailLocalPartTransform().apply(firstName + "." + lastName);
        String domain = rng.pick(locale.emailDomains());
        return localPart + "@" + domain;
    }

    /** Infinite stream of randomized emails. Use {@code .limit(n)} to bound. Not parallel-safe. */
    public Stream<String> stream() {
        return Stream.generate(this::email);
    }

    /** Eagerly produces {@code count} emails. */
    public List<String> list(int count) {
        return stream().limit(count).toList();
    }
}

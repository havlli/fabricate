package org.fabricate.provider;

import java.util.List;
import java.util.stream.Stream;
import org.fabricate.random.Rng;
import org.fabricate.spi.LocaleData;

public final class JobTitles {

    private final LocaleData locale;
    private final Rng rng;

    public JobTitles(LocaleData locale, Rng rng) {
        this.locale = locale;
        this.rng = rng;
    }

    public String jobTitle() {
        return rng.pick(locale.jobTitles());
    }

    /** Infinite stream of job titles. Use {@code .limit(n)} to bound. Not parallel-safe. */
    public Stream<String> stream() {
        return Stream.generate(this::jobTitle);
    }

    /** Eagerly produces {@code count} job titles. */
    public List<String> list(int count) {
        return stream().limit(count).toList();
    }
}

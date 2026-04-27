package org.fabricate.provider;

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
}

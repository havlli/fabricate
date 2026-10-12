package dev.havlli.fabricate.locale;

import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;

public final class GermanLocaleProvider implements LocaleProvider {
    public GermanLocaleProvider() {}

    @Override
    public LocaleData get() {
        return GermanLocale.INSTANCE;
    }
}

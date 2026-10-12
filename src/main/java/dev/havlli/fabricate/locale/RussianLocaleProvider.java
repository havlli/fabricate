package dev.havlli.fabricate.locale;

import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;

public final class RussianLocaleProvider implements LocaleProvider {
    public RussianLocaleProvider() {}

    @Override
    public LocaleData get() {
        return RussianLocale.INSTANCE;
    }
}

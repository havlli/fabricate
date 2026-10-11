package dev.havlli.fabricate.locale;

import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;

public final class EnglishLocaleProvider implements LocaleProvider {
    public EnglishLocaleProvider() {}

    @Override
    public LocaleData get() {
        return EnglishLocale.INSTANCE;
    }
}

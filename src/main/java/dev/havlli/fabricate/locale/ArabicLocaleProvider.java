package dev.havlli.fabricate.locale;

import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;

public final class ArabicLocaleProvider implements LocaleProvider {
    public ArabicLocaleProvider() {}

    @Override
    public LocaleData get() {
        return ArabicLocale.INSTANCE;
    }
}

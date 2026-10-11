package dev.havlli.fabricate.locale;

import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;

public final class HindiLocaleProvider implements LocaleProvider {
    public HindiLocaleProvider() {}

    @Override
    public LocaleData get() {
        return HindiLocale.INSTANCE;
    }
}

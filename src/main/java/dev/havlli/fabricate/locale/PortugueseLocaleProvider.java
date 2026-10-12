package dev.havlli.fabricate.locale;

import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;

public final class PortugueseLocaleProvider implements LocaleProvider {
    public PortugueseLocaleProvider() {}

    @Override
    public LocaleData get() {
        return PortugueseLocale.INSTANCE;
    }
}

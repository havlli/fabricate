package dev.havlli.fabricate.internal.locale;

import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;

public final class JapaneseLocaleProvider implements LocaleProvider {
    public JapaneseLocaleProvider() {}

    @Override
    public LocaleData get() {
        return JapaneseLocale.INSTANCE;
    }
}

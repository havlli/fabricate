package dev.havlli.fabricate.locale;

import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;

public final class ChineseLocaleProvider implements LocaleProvider {
    public ChineseLocaleProvider() {}

    @Override
    public LocaleData get() {
        return ChineseLocale.INSTANCE;
    }
}

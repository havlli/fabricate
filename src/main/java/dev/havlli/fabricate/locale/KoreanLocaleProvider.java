package dev.havlli.fabricate.locale;

import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;

public final class KoreanLocaleProvider implements LocaleProvider {
    public KoreanLocaleProvider() {}

    @Override
    public LocaleData get() {
        return KoreanLocale.INSTANCE;
    }
}

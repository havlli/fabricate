package org.fabricate.locale;

import org.fabricate.spi.LocaleData;
import org.fabricate.spi.LocaleProvider;

public final class KoreanLocaleProvider implements LocaleProvider {
    public KoreanLocaleProvider() {}

    @Override
    public LocaleData get() {
        return KoreanLocale.INSTANCE;
    }
}

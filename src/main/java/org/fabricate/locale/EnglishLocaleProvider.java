package org.fabricate.locale;

import org.fabricate.spi.LocaleData;
import org.fabricate.spi.LocaleProvider;

public final class EnglishLocaleProvider implements LocaleProvider {
    @Override
    public LocaleData get() {
        return EnglishLocale.INSTANCE;
    }
}

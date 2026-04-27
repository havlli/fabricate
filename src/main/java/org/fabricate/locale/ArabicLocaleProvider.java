package org.fabricate.locale;

import org.fabricate.spi.LocaleData;
import org.fabricate.spi.LocaleProvider;

public final class ArabicLocaleProvider implements LocaleProvider {
    public ArabicLocaleProvider() {}

    @Override
    public LocaleData get() {
        return ArabicLocale.INSTANCE;
    }
}

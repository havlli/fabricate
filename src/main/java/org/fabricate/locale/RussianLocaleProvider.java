package org.fabricate.locale;

import org.fabricate.spi.LocaleData;
import org.fabricate.spi.LocaleProvider;

public final class RussianLocaleProvider implements LocaleProvider {
    public RussianLocaleProvider() {}

    @Override
    public LocaleData get() {
        return RussianLocale.INSTANCE;
    }
}

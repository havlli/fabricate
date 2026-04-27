package org.fabricate.locale;

import org.fabricate.spi.LocaleData;
import org.fabricate.spi.LocaleProvider;

public final class HindiLocaleProvider implements LocaleProvider {
    public HindiLocaleProvider() {}

    @Override
    public LocaleData get() {
        return HindiLocale.INSTANCE;
    }
}

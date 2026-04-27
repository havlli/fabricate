package org.fabricate.locale;

import org.fabricate.spi.LocaleData;
import org.fabricate.spi.LocaleProvider;

public final class ChineseLocaleProvider implements LocaleProvider {
    public ChineseLocaleProvider() {}

    @Override
    public LocaleData get() {
        return ChineseLocale.INSTANCE;
    }
}

package org.fabricate.locale;

import org.fabricate.spi.LocaleData;
import org.fabricate.spi.LocaleProvider;

public final class JapaneseLocaleProvider implements LocaleProvider {
    public JapaneseLocaleProvider() {}

    @Override
    public LocaleData get() {
        return JapaneseLocale.INSTANCE;
    }
}

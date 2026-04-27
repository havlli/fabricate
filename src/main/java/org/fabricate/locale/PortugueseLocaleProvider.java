package org.fabricate.locale;

import org.fabricate.spi.LocaleData;
import org.fabricate.spi.LocaleProvider;

public final class PortugueseLocaleProvider implements LocaleProvider {
    public PortugueseLocaleProvider() {}

    @Override
    public LocaleData get() {
        return PortugueseLocale.INSTANCE;
    }
}

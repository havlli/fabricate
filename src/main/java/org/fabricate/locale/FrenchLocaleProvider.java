package org.fabricate.locale;

import org.fabricate.spi.LocaleData;
import org.fabricate.spi.LocaleProvider;

public final class FrenchLocaleProvider implements LocaleProvider {
    public FrenchLocaleProvider() {}

    @Override
    public LocaleData get() {
        return FrenchLocale.INSTANCE;
    }
}

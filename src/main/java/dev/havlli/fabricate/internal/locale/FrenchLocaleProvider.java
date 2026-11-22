package dev.havlli.fabricate.internal.locale;

import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;

public final class FrenchLocaleProvider implements LocaleProvider {
    public FrenchLocaleProvider() {}

    @Override
    public LocaleData get() {
        return FrenchLocale.INSTANCE;
    }
}

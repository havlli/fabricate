package org.fabricate.locale;

import org.fabricate.spi.LocaleData;
import org.fabricate.spi.LocaleProvider;

public final class SpanishLocaleProvider implements LocaleProvider {
    public SpanishLocaleProvider() {}

    @Override
    public LocaleData get() {
        return SpanishLocale.INSTANCE;
    }
}

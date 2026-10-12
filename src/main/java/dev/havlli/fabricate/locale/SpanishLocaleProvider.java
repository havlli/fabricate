package dev.havlli.fabricate.locale;

import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;

public final class SpanishLocaleProvider implements LocaleProvider {
    public SpanishLocaleProvider() {}

    @Override
    public LocaleData get() {
        return SpanishLocale.INSTANCE;
    }
}

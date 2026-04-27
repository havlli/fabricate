package org.fabricate.locale;

import org.fabricate.spi.LocaleData;
import org.fabricate.spi.LocaleProvider;

public final class GermanLocaleProvider implements LocaleProvider {
    public GermanLocaleProvider() {}

    @Override
    public LocaleData get() {
        return GermanLocale.INSTANCE;
    }
}

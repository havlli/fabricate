package org.fabricate.locale;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.Set;
import org.fabricate.spi.LocaleData;
import org.fabricate.spi.LocaleProvider;

/**
 * Discovers built-in and user-supplied {@link LocaleData} implementations.
 *
 * Resolution order on lookup:
 * <ol>
 *   <li>Exact {@link Locale} match (e.g. {@code zh_CN} when registered).</li>
 *   <li>Language-only fallback (e.g. {@code zh_HK} resolves to {@code zh}
 *       if no exact match exists).</li>
 *   <li>Throws {@link NoSuchElementException} otherwise.</li>
 * </ol>
 *
 * Built-in locales (English, Chinese) are always available without any
 * service registration. Additional locales can be contributed via the
 * {@link LocaleProvider} service-loader interface and will override
 * built-ins for the same {@link Locale}.
 */
public final class LocaleRegistry {

    private static final LocaleRegistry DEFAULT = new LocaleRegistry(loadAll());

    private final Map<Locale, LocaleData> byLocale;

    LocaleRegistry(Map<Locale, LocaleData> byLocale) {
        this.byLocale = Map.copyOf(byLocale);
    }

    public static LocaleRegistry getDefault() {
        return DEFAULT;
    }

    public LocaleData get(Locale locale) {
        LocaleData exact = byLocale.get(locale);
        if (exact != null) {
            return exact;
        }
        Locale languageOnly = Locale.forLanguageTag(locale.getLanguage());
        LocaleData fallback = byLocale.get(languageOnly);
        if (fallback != null) {
            return fallback;
        }
        throw new NoSuchElementException("No LocaleData registered for " + locale.toLanguageTag()
                + " (available: " + byLocale.keySet() + ")");
    }

    public Set<Locale> available() {
        return byLocale.keySet();
    }

    private static Map<Locale, LocaleData> loadAll() {
        Map<Locale, LocaleData> map = new LinkedHashMap<>();
        map.put(EnglishLocale.INSTANCE.locale(), EnglishLocale.INSTANCE);
        map.put(ChineseLocale.INSTANCE.locale(), ChineseLocale.INSTANCE);
        for (LocaleProvider provider : ServiceLoader.load(LocaleProvider.class)) {
            LocaleData data = provider.get();
            map.put(data.locale(), data);
        }
        return new HashMap<>(map);
    }
}

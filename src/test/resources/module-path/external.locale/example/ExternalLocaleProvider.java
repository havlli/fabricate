package example;

import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

public final class ExternalLocaleProvider implements LocaleProvider {
    @Override
    public LocaleData get() {
        return new Data();
    }

    public static final class Data implements LocaleData {
        @Override public Locale locale() { return Locale.forLanguageTag("zz"); }
        @Override public String nameDelimiter() { return "~"; }
        @Override public UnaryOperator<String> emailLocalPartTransform() { return String::toLowerCase; }
        @Override public List<String> firstNames() { return List.of("External"); }
        @Override public List<String> lastNames() { return List.of("Locale"); }
        @Override public List<String> emailDomains() { return List.of("external.example"); }
        @Override public List<String> phoneCountryCodes() { return List.of("+99"); }
        @Override public List<String> streets() { return List.of("External Street"); }
        @Override public List<String> cities() { return List.of("External City"); }
        @Override public List<String> states() { return List.of("External State"); }
        @Override public List<String> postalCodes() { return List.of("12345"); }
        @Override public List<String> countries() { return List.of("External Country"); }
        @Override public List<String> jobTitles() { return List.of("External Job"); }
    }
}

package org.fabricate.locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.UnaryOperator;
import org.fabricate.spi.LocaleData;
import org.junit.jupiter.api.Test;

class LocaleRegistryTest {

    @Test
    void getDefault_includesBuiltInLocales() {
        LocaleRegistry registry = LocaleRegistry.getDefault();

        assertThat(registry.available()).contains(Locale.ENGLISH, Locale.CHINESE);
        assertThat(registry.get(Locale.ENGLISH)).isEqualTo(EnglishLocale.INSTANCE);
        assertThat(registry.get(Locale.CHINESE)).isEqualTo(ChineseLocale.INSTANCE);
    }

    @Test
    void get_unknownLocale_throws() {
        LocaleRegistry registry = LocaleRegistry.getDefault();

        assertThatThrownBy(() -> registry.get(Locale.JAPANESE))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("ja");
    }

    @Test
    void customRegistry_canAddNewLocale() {
        LocaleData stub = new StubLocale(Locale.JAPANESE);
        LocaleRegistry registry = new LocaleRegistry(Map.of(Locale.JAPANESE, stub));

        assertThat(registry.get(Locale.JAPANESE)).isSameAs(stub);
    }

    private record StubLocale(Locale locale) implements LocaleData {
        @Override public String nameDelimiter() { return " "; }
        @Override public UnaryOperator<String> emailLocalPartTransform() { return s -> s; }
        @Override public List<String> firstNames() { return List.of("X"); }
        @Override public List<String> lastNames() { return List.of("Y"); }
        @Override public List<String> emailDomains() { return List.of("example.jp"); }
        @Override public List<String> phoneCountryCodes() { return List.of("+81"); }
        @Override public List<String> streets() { return List.of("S"); }
        @Override public List<String> cities() { return List.of("C"); }
        @Override public List<String> states() { return List.of("ST"); }
        @Override public List<String> postalCodes() { return List.of("000"); }
        @Override public List<String> countries() { return List.of("Japan"); }
        @Override public List<String> jobTitles() { return List.of("J"); }
    }
}

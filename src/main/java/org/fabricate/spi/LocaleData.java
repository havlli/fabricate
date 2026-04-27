package org.fabricate.spi;

import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

/**
 * Locale-specific data and rules consumed by built-in providers.
 *
 * Implementations are typically immutable singletons. Built-in locales
 * live in {@code org.fabricate.locale}; new locales are contributed via
 * {@link LocaleProvider} on the module path or classpath.
 */
public interface LocaleData {

    Locale locale();

    String nameDelimiter();

    UnaryOperator<String> emailLocalPartTransform();

    List<String> firstNames();

    List<String> lastNames();

    List<String> emailDomains();

    List<String> phoneCountryCodes();

    List<String> streets();

    List<String> cities();

    List<String> states();

    List<String> postalCodes();

    List<String> countries();

    List<String> jobTitles();
}

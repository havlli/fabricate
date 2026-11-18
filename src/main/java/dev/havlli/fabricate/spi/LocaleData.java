package dev.havlli.fabricate.spi;

import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

/**
 * Locale-specific data and rules consumed by built-in providers.
 *
 * Implementations are typically immutable singletons. Built-in locales
 * live in {@code dev.havlli.fabricate.locale}; new locales are contributed via
 * {@link LocaleProvider} on the module path or classpath.
 *
 * <p><strong>Consumer-implemented type.</strong> Third parties implement this
 * interface. New methods must be {@code default}; no abstract methods will be
 * added before 1.0.
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

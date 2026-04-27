package org.fabricate.spi;

/**
 * Service-loader contract for contributing locales.
 *
 * Implementations are discovered via {@link java.util.ServiceLoader} both
 * on the module path (declared in {@code module-info.java}) and on the
 * classpath (registered under {@code META-INF/services}).
 */
public interface LocaleProvider {
    LocaleData get();
}

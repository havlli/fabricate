package dev.havlli.fabricate.spi;

/**
 * Service-loader contract for contributing locales.
 *
 * Implementations are discovered via {@link java.util.ServiceLoader} both
 * on the module path (declared in {@code module-info.java}) and on the
 * classpath (registered under {@code META-INF/services}).
 *
 * <p><strong>Consumer-implemented type.</strong> Third parties implement this
 * interface. New methods must be {@code default}; no abstract methods will be
 * added before 1.0.
 */
public interface LocaleProvider {
    LocaleData get();
}

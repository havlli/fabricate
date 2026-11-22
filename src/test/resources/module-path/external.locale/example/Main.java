package example;

import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.spi.LocaleData;
import dev.havlli.fabricate.spi.LocaleProvider;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

public final class Main {
    public static void main(String[] args) {
        check(Fabricate.class.getModule().isNamed(), "fabricate must be a named module");
        check(Main.class.getModule().isNamed(), "fixture must be a named module");
        check(Fabricate.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar"),
                "must run against the packaged jar");
        var exports = Fabricate.class.getModule().getDescriptor().exports().stream()
                .map(e -> e.source()).collect(Collectors.toSet());
        check(exports.equals(Set.of("dev.havlli.fabricate", "dev.havlli.fabricate.junit",
                "dev.havlli.fabricate.model", "dev.havlli.fabricate.sink", "dev.havlli.fabricate.spi")),
                "only the supported API packages may be exported");
        LocaleData data = ServiceLoader.load(LocaleProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .filter(p -> p instanceof ExternalLocaleProvider)
                .findFirst().orElseThrow().get();
        check(Fabricate.availableLocales().contains(data.locale()), "external service must be discovered");
        check(Fabricate.availableLocales().containsAll(Set.of(Locale.ENGLISH, Locale.CHINESE, Locale.GERMAN)),
                "built-in services must remain available");
        Fabricate discovered = Fabricate.builder().locale(data.locale()).seed(42).build();
        Fabricate supplied = Fabricate.builder().locale(Locale.ENGLISH).locale(data).seed(42).build();
        check(supplied.locale() == data, "builder must retain the supplied third-party instance");
        check(supplied.name().full().equals("External~Locale"), "custom names must be used");
        check(discovered.name().full().equals("External~Locale"), "service names must be used");
        check(supplied.person().list(10).equals(discovered.person().list(10)), "seeded SPI parity");
        check(Fabricate.builder().locale(data).locale(Locale.ENGLISH).build().locale().locale()
                .equals(Locale.ENGLISH), "last locale selection must win");
        try {
            Fabricate.builder().locale((LocaleData) null);
            throw new AssertionError("null locale data must be rejected");
        } catch (NullPointerException expected) {
            // Expected at the builder call, rather than at build time.
        }
        System.out.println("External module SPI and encapsulation verified");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}

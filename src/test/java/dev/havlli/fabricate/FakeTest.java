package dev.havlli.fabricate;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("removal") // Compatibility coverage for the deprecated global mutators.
class FakeTest {

    @AfterEach
    void resetDefaults() {
        Fake.seed(Fake.DEFAULT_SEED);
        Fake.locale(Locale.ENGLISH);
    }

    @Test
    void defaultSeed_producesDeterministicFirstCall() {
        Fake.seed(Fake.DEFAULT_SEED);
        String first = Fake.firstName();

        Fake.seed(Fake.DEFAULT_SEED);
        String firstAgain = Fake.firstName();

        assertThat(firstAgain).isEqualTo(first);
    }

    @Test
    void seed_changeAltersOutput() {
        Fake.seed(0L);
        String a = Fake.fullName();

        Fake.seed(123L);
        String b = Fake.fullName();

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void locale_switchesActiveLocale() {
        Fake.locale(Locale.CHINESE);

        assertThat(Fake.fabricate().locale().locale()).isEqualTo(Locale.CHINESE);
        assertThat(Fake.fullName()).doesNotContain(" ");
    }

    @Test
    void locale_preservesExplicitSeed() {
        Fake.seed(42L);
        Fake.fullName();

        for (Locale locale : List.of(Locale.CHINESE, Locale.ENGLISH)) {
            Fake.locale(locale);
            Fabricate expected = Fabricate.builder().seed(42L).locale(locale).build();
            for (int i = 0; i < 10; i++) {
                assertThat(Fake.fullName()).isEqualTo(expected.name().full());
                assertThat(Fake.uuid()).isEqualTo(expected.identity().uuid());
            }
        }
    }

    @Test
    void locale_preservesUnseededMode() {
        Fake.unseed();
        Fake.locale(Locale.CHINESE);
        List<UUID> first = IntStream.range(0, 10).mapToObj(i -> Fake.uuid()).toList();

        Fake.locale(Locale.CHINESE);
        List<UUID> second = IntStream.range(0, 10).mapToObj(i -> Fake.uuid()).toList();

        assertThat(second).isNotEqualTo(first);
    }

    @Test
    void locale_preservesDefaultSeed() {
        resetDefaults();
        Fake.locale(Locale.CHINESE);
        Fabricate expected = Fabricate.builder().seed(Fake.DEFAULT_SEED)
                .locale(Locale.CHINESE).build();

        for (int i = 0; i < 10; i++) {
            assertThat(Fake.fullName()).isEqualTo(expected.name().full());
            assertThat(Fake.uuid()).isEqualTo(expected.identity().uuid());
        }
    }

    @Test
    void primitives_areCallableWithoutSetup() {
        Fake.seed(7L);

        assertThat(Fake.intBetween(1, 100)).isBetween(1, 100);
        assertThat(Fake.email()).contains("@");
        assertThat(Fake.uuid()).isNotNull();
        assertThat(Fake.sentence()).endsWith(".");
        assertThat(Fake.password(20)).hasSize(20);
    }

    @Test
    void use_swapsInACustomFabricate() {
        Fabricate custom = Fabricate.builder().seed(42L).build();
        Fake.use(custom);

        assertThat(Fake.fabricate()).isSameAs(custom);
    }

    @Test
    void shortcuts_matchLocalFluentDraws() {
        Fake.seed(42L);
        Fabricate expected = Fabricate.builder().seed(42L).build();

        for (int i = 0; i < 10; i++) {
            assertThat(Fake.first()).isEqualTo(expected.name().first());
            assertThat(Fake.last()).isEqualTo(expected.name().last());
            assertThat(Fake.full()).isEqualTo(expected.name().full());
            assertThat(Fake.title()).isEqualTo(expected.job().title());
            assertThat(Fake.between(-10, 10)).isEqualTo(expected.number().between(-10, 10));
            assertThat(Fake.between(-100L, 100L)).isEqualTo(expected.number().between(-100L, 100L));
            assertThat(Fake.between(-1.0, 1.0)).isEqualTo(expected.number().between(-1.0, 1.0));
        }
    }

    @Test
    void oldShortcuts_keepMatchingTheNewNames() {
        Fake.seed(7L);
        List<Object> old = List.of(Fake.firstName(), Fake.lastName(), Fake.fullName(), Fake.jobTitle(),
                Fake.intBetween(1, 10), Fake.longBetween(1L, 10L), Fake.doubleBetween(1.0, 10.0));
        Fake.seed(7L);
        assertThat(List.of(Fake.first(), Fake.last(), Fake.full(), Fake.title(),
                Fake.between(1, 10), Fake.between(1L, 10L), Fake.between(1.0, 10.0))).isEqualTo(old);
    }

    @Test
    void mutators_areDeprecatedForRemovalAndDocumentLocalConfiguration() throws Exception {
        String source = Files.readString(Path.of(System.getProperty("basedir", "."),
                "src/main/java/dev/havlli/fabricate/Fake.java"));
        for (var method : List.of(Fake.class.getMethod("seed", long.class),
                Fake.class.getMethod("unseed"), Fake.class.getMethod("locale", Locale.class),
                Fake.class.getMethod("use", Fabricate.class))) {
            Deprecated deprecated = method.getAnnotation(Deprecated.class);
            assertThat(deprecated).as(method.getName()).isNotNull();
            assertThat(deprecated.since()).isEqualTo("0.2.0");
            assertThat(deprecated.forRemoval()).isTrue();

            int declaration = source.indexOf("public static void " + method.getName() + "(");
            String javadoc = source.substring(source.lastIndexOf("/**", declaration), declaration);
            assertThat(javadoc).contains("@deprecated", "Fabricate#builder()", "#fabricate()",
                    "only to read the default instance");
        }
    }
}

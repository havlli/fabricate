package dev.havlli.fabricate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

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
                assertThat(Fake.fullName()).isEqualTo(expected.names().fullName());
                assertThat(Fake.uuid()).isEqualTo(expected.identities().uuid());
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
            assertThat(Fake.fullName()).isEqualTo(expected.names().fullName());
            assertThat(Fake.uuid()).isEqualTo(expected.identities().uuid());
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
}

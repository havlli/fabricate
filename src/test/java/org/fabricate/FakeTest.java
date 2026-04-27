package org.fabricate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
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

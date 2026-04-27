package org.fabricate.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.fabricate.Fabricate;
import org.fabricate.model.Address;
import org.fabricate.model.Person;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FabricateExtensionTest {

    @Nested
    @FabricateTest(seed = 42L)
    class WithClassLevelSeed {

        @Test
        void resolvesFabricateParameter(Fabricate fab) {
            assertThat(fab).isNotNull();
            assertThat(fab.locale().locale()).isEqualTo(Locale.ENGLISH);
        }

        @Test
        void seedIsAppliedConsistentlyAcrossInvocations(Fabricate fab) {
            // The class-level seed=42 means each test gets a fresh Fabricate
            // seeded the same way; the first name drawn must therefore match
            // a separately-built Fabricate using the same seed.
            String drawnHere = fab.names().fullName();
            String drawnIndependently = Fabricate.builder().seed(42L).build().names().fullName();

            assertThat(drawnHere).isEqualTo(drawnIndependently);
        }

        @Test
        void resolvesPersonAndAddress(Person person, Address address) {
            assertThat(person.firstName()).isNotBlank();
            assertThat(address.country()).isNotBlank();
        }

        @Test
        @Seed(99L)
        void methodSeedOverridesClassSeed(Fabricate fab) {
            String drawn = fab.names().fullName();
            String expected = Fabricate.builder().seed(99L).build().names().fullName();

            assertThat(drawn).isEqualTo(expected);
        }
    }

    @Nested
    @FabricateTest(seed = 1L, locale = "zh")
    class WithChineseLocale {

        @Test
        void localeIsHonoured(Fabricate fab) {
            assertThat(fab.locale().locale()).isEqualTo(Locale.CHINESE);
            assertThat(fab.locale().nameDelimiter()).isEmpty();
        }
    }
}

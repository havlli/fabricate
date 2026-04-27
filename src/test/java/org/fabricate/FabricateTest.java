package org.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import org.fabricate.model.Person;
import org.junit.jupiter.api.Test;

class FabricateTest {

    @Test
    void create_returnsEnglishLocaleByDefault() {
        Fabricate fab = Fabricate.create();

        assertThat(fab.locale().locale()).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void seed_producesIdenticalSequenceAcrossInstances() {
        Fabricate a = Fabricate.builder().seed(42L).build();
        Fabricate b = Fabricate.builder().seed(42L).build();

        for (int i = 0; i < 50; i++) {
            assertThat(a.names().fullName()).isEqualTo(b.names().fullName());
        }
    }

    @Test
    void differentSeeds_eventuallyDiverge() {
        Fabricate a = Fabricate.builder().seed(1L).build();
        Fabricate b = Fabricate.builder().seed(2L).build();

        boolean differs = IntStream.range(0, 50)
                .anyMatch(i -> !a.names().fullName().equals(b.names().fullName()));
        assertThat(differs).isTrue();
    }

    @Test
    void chineseLocale_isResolved() {
        Fabricate fab = Fabricate.builder().locale(Locale.CHINESE).build();

        assertThat(fab.locale().locale()).isEqualTo(Locale.CHINESE);
        assertThat(fab.locale().nameDelimiter()).isEmpty();
    }

    @Test
    void unsupportedLocale_throws() {
        assertThatThrownBy(() -> Fabricate.builder().locale(Locale.JAPANESE).build())
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("ja");
    }

    @Test
    void availableLocales_includesBuiltIns() {
        assertThat(Fabricate.availableLocales()).contains(Locale.ENGLISH, Locale.CHINESE);
    }

    @Test
    void person_hasAllRequiredFieldsPopulated() {
        Fabricate fab = Fabricate.builder().seed(7L).build();

        Person p = fab.persons().person();

        assertThat(p.firstName()).isNotBlank();
        assertThat(p.lastName()).isNotBlank();
        assertThat(p.email()).contains("@");
        assertThat(p.uid()).isNotBlank();
        assertThat(p.username()).isNotBlank();
        assertThat(p.phoneNumber()).startsWith("+");
        assertThat(p.address()).isNotNull();
        assertThat(p.dateOfBirth()).isNotNull();
        assertThat(p.jobTitle()).isNotBlank();
    }
}

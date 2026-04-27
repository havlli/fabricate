package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.fabricate.Fabricate;
import org.fabricate.locale.EnglishLocale;
import org.fabricate.model.Address;
import org.fabricate.model.Person;
import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class StreamApiTest {

    @Test
    void names_listIsBoundedAndReproducible() {
        List<String> a = new Names(EnglishLocale.INSTANCE, Rng.seeded(1L)).list(20);
        List<String> b = new Names(EnglishLocale.INSTANCE, Rng.seeded(1L)).list(20);

        assertThat(a).hasSize(20).isEqualTo(b);
    }

    @Test
    void names_streamIsLazyAndUnbounded() {
        Names names = new Names(EnglishLocale.INSTANCE, Rng.seeded(1L));

        List<String> head = names.stream().limit(5).toList();

        assertThat(head).hasSize(5);
    }

    @Test
    void emails_listAllContainAtSign() {
        List<String> emails = new Emails(EnglishLocale.INSTANCE, Rng.seeded(2L)).list(15);

        assertThat(emails).hasSize(15).allMatch(e -> e.contains("@"));
    }

    @Test
    void phones_listAllStartWithPlus() {
        List<String> phones = new Phones(EnglishLocale.INSTANCE, Rng.seeded(3L)).list(10);

        assertThat(phones).hasSize(10).allMatch(p -> p.startsWith("+"));
    }

    @Test
    void addresses_listAllNonNullFields() {
        List<Address> addresses = new Addresses(EnglishLocale.INSTANCE, Rng.seeded(4L)).list(10);

        assertThat(addresses).hasSize(10).allSatisfy(a -> {
            assertThat(a.street()).isNotBlank();
            assertThat(a.city()).isNotBlank();
            assertThat(a.country()).isNotBlank();
        });
    }

    @Test
    void datesOfBirth_listAllInRange() {
        Clock fixed = Clock.fixed(LocalDate.of(2026, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
        DatesOfBirth dob = new DatesOfBirth(Rng.seeded(5L), fixed);

        List<LocalDate> dates = dob.list(25);

        LocalDate today = LocalDate.of(2026, 1, 1);
        assertThat(dates).hasSize(25).allSatisfy(d -> {
            assertThat(d).isBefore(today.minusYears(17));
            assertThat(d).isAfter(today.minusYears(91));
        });
    }

    @Test
    void jobTitles_listIsBounded() {
        List<String> titles = new JobTitles(EnglishLocale.INSTANCE, Rng.seeded(6L)).list(8);

        assertThat(titles).hasSize(8).allMatch(t -> !t.isBlank());
    }

    @Test
    void identities_streamProducesUniqueUuidsUnderASeed() {
        List<UUID> uuids = new Identities(Rng.seeded(7L)).list(50);

        assertThat(uuids).hasSize(50).doesNotHaveDuplicates();
    }

    @Test
    void persons_streamProducesFullyPopulatedRecords() {
        Fabricate fab = Fabricate.builder().seed(8L).build();

        List<Person> people = fab.persons().list(12);

        assertThat(people).hasSize(12).allSatisfy(p -> {
            assertThat(p.firstName()).isNotBlank();
            assertThat(p.lastName()).isNotBlank();
            assertThat(p.email()).contains("@");
            assertThat(p.address()).isNotNull();
        });
    }

    @Test
    void persons_listIsReproducibleAcrossInstances() {
        List<Person> a = Fabricate.builder().seed(9L).build().persons().list(5);
        List<Person> b = Fabricate.builder().seed(9L).build().persons().list(5);

        assertThat(a).isEqualTo(b);
    }
}

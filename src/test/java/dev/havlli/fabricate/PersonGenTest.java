package dev.havlli.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import dev.havlli.fabricate.model.Address;
import dev.havlli.fabricate.model.Person;
import org.junit.jupiter.api.Test;

@SuppressWarnings("removal") // Compare against the legacy builder's semantics.
class PersonGenTest {

    private static Fabricate seeded() {
        return Fabricate.builder().seed(42L).build();
    }

    @Test
    void firstNameOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(PersonGen::firstName, PersonGen::firstName,
                (fab, value) -> fab.persons().builder().firstName(value).build(),
                "Ada", "Grace");
    }

    @Test
    void lastNameOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(PersonGen::lastName, PersonGen::lastName,
                (fab, value) -> fab.persons().builder().lastName(value).build(),
                "Lovelace", "Hopper");
    }

    @Test
    void emailOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(PersonGen::email, PersonGen::email,
                (fab, value) -> fab.persons().builder().email(value).build(),
                "ada@example.test", "grace@example.test");
    }

    @Test
    void uidOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(PersonGen::uid, PersonGen::uid,
                (fab, value) -> fab.persons().builder().uid(value).build(),
                UUID.fromString("12345678-1234-5678-9abc-123456789abc"), UUID.fromString("87654321-1234-5678-9abc-123456789abc"));
    }

    @Test
    void usernameOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(PersonGen::username, PersonGen::username,
                (fab, value) -> fab.persons().builder().username(value).build(),
                "ada", "grace");
    }

    @Test
    void phoneNumberOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(PersonGen::phoneNumber, PersonGen::phoneNumber,
                (fab, value) -> fab.persons().builder().phoneNumber(value).build(),
                "111", "222");
    }

    @Test
    void addressOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(PersonGen::address, PersonGen::address,
                (fab, value) -> fab.persons().builder().address(value).build(),
                new Address("s", "c", "st", "p", "co"), new Address("s2", "c2", "st2", "p2", "co2"));
    }

    @Test
    void dateOfBirthOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(PersonGen::dateOfBirth, PersonGen::dateOfBirth,
                (fab, value) -> fab.persons().builder().dateOfBirth(value).build(),
                LocalDate.of(1990, 1, 2), LocalDate.of(2000, 3, 4));
    }

    @Test
    void jobTitleOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(PersonGen::jobTitle, PersonGen::jobTitle,
                (fab, value) -> fab.persons().builder().jobTitle(value).build(),
                "CEO", "CTO");
    }

    private static <T> void checkOverride(
            BiFunction<PersonGen, T, PersonGen> fixed,
            BiFunction<PersonGen, Gen<? extends T>, PersonGen> generated,
            BiFunction<Fabricate, T, Person> expected, T first, T second) {
        Fabricate fab = seeded();
        Fabricate reference = seeded();
        PersonGen base = fab.person();
        PersonGen overridden = fixed.apply(base, first);
        AtomicInteger calls = new AtomicInteger();
        PersonGen dynamic = generated.apply(base, fab.gen(() -> calls.incrementAndGet() == 1 ? first : second));
        assertThat(overridden).isNotSameAs(base);
        assertThat(dynamic).isNotSameAs(base);
        assertThat(calls).hasValue(0);
        assertThat(base.get()).isEqualTo(reference.persons().person());
        assertThat(overridden.get()).isEqualTo(expected.apply(reference, first));
        assertThat(dynamic.get()).isEqualTo(expected.apply(reference, first));
        assertThat(dynamic.get()).isEqualTo(expected.apply(reference, second));
        assertThat(calls).hasValue(2);
        PersonGen replaced = fixed.apply(overridden, second);
        assertThat(replaced.get()).isEqualTo(expected.apply(reference, second));
        assertThat(overridden.get()).isEqualTo(expected.apply(reference, first));
        assertThat(fab.rng().nextInt(1_000_000)).isEqualTo(reference.rng().nextInt(1_000_000));
        assertThatThrownBy(() -> fixed.apply(base, null))
                .isInstanceOf(NullPointerException.class).hasMessage("value");
        assertThatThrownBy(() -> generated.apply(base, null))
                .isInstanceOf(NullPointerException.class).hasMessage("gen");
    }

    @Test
    void stringUidIsValidatedEagerlyAndStoredAsUuidText() {
        PersonGen base = seeded().person();
        String value = "ABCDEF01-1234-5678-9ABC-123456789ABC";
        PersonGen configured = base.uid(value);
        assertThat(configured).isNotSameAs(base);
        assertThat(base.get()).isEqualTo(seeded().person().get());
        Person person = configured.get();
        assertThat(person.uid()).isEqualTo(UUID.fromString(value).toString());
        assertThat(person.username()).endsWith("abcdef01");
        Fabricate reference = seeded();
        assertThat(seeded().person().uid(value).get())
                .isEqualTo(reference.persons().builder().uid(UUID.fromString(value)).build());
        assertThatThrownBy(() -> base.uid("not-a-uuid"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("not-a-uuid");
        assertThatThrownBy(() -> base.uid((String) null))
                .isInstanceOf(NullPointerException.class).hasMessage("value");
    }

    @Test
    void namesAndUidDriveEmailAndUsernameUnlessOverridden() {
        Fabricate fab = seeded();
        UUID uid = UUID.fromString("12345678-1234-5678-9abc-123456789abc");
        PersonGen gen = fab.person().firstName(fab.oneOf("Ada")).lastName("Lovelace").uid(uid);
        Person person = gen.get();
        assertThat(person.email()).startsWith("ada.lovelace@");
        assertThat(person.username()).isEqualTo("adal12345678");
        Person custom = gen.email("fixed@example.test").username("fixed").get();
        assertThat(custom.email()).isEqualTo("fixed@example.test");
        assertThat(custom.username()).isEqualTo("fixed");
    }

    @Test
    void nestedAddressGeneratorsAreDrawnForEveryPerson() {
        Fabricate fab = seeded();
        AtomicInteger calls = new AtomicInteger();
        AddressGen address = fab.address().city("Berlin")
                .street(fab.gen(() -> "Street " + calls.incrementAndGet()));
        PersonGen gen = fab.person().address(address);
        assertThat(calls).hasValue(0);
        List<Person> berliners = gen.list(5);
        assertThat(berliners).hasSize(5).allSatisfy(p -> assertThat(p.address().city()).isEqualTo("Berlin"));
        assertThat(berliners).extracting(p -> p.address().street())
                .containsExactly("Street 1", "Street 2", "Street 3", "Street 4", "Street 5");
    }

    @Test
    void defaultsMatchLegacyDrawForDrawAcrossLocalesAndSeeds() {
        for (var locale : Fabricate.availableLocales()) {
            for (long seed : new long[]{0, 42, -7}) {
                Fabricate fab = Fabricate.builder().locale(locale).seed(seed).build();
                Fabricate reference = Fabricate.builder().locale(locale).seed(seed).build();
                PersonGen gen = fab.person();
                for (int i = 0; i < 20; i++) {
                    assertThat(gen.get()).isEqualTo(reference.persons().person());
                    assertThat(fab.rng().nextInt(1_000_000)).isEqualTo(reference.rng().nextInt(1_000_000));
                }
            }
        }
    }

    @Test
    void supportsListsLazyStreamsAndUnique() {
        List<Person> people = seeded().person().jobTitle("CEO").list(5);
        assertThat(people).hasSize(5).allSatisfy(p -> assertThat(p.jobTitle()).isEqualTo("CEO"));
        assertThatThrownBy(() -> people.add(people.getFirst())).isInstanceOf(UnsupportedOperationException.class);
        Fabricate fab = seeded();
        AtomicInteger calls = new AtomicInteger();
        var stream = fab.person().jobTitle(fab.gen(() -> "job-" + calls.incrementAndGet())).stream();
        assertThat(calls).hasValue(0);
        assertThat(stream.limit(3).toList()).extracting(Person::jobTitle).containsExactly("job-1", "job-2", "job-3");
        PersonGen constant = fixedPerson(fab);
        Gen<Person> unique = constant.unique();
        assertThat(unique.get()).isEqualTo(constant.get());
        assertThatThrownBy(unique::get).isInstanceOf(UniqueExhaustedException.class);
        assertThat(constant.unique().get()).isEqualTo(constant.get());
    }

    @Test
    void allFixedFieldsConsumeNoRandomness() {
        Fabricate fab = seeded();
        fixedPerson(fab).list(5);
        assertThat(fab.rng().nextInt(1_000_000)).isEqualTo(seeded().rng().nextInt(1_000_000));
    }

    private static PersonGen fixedPerson(Fabricate fab) {
        return fab.person().firstName("Ada").lastName("Lovelace").email("ada@example.test")
                .uid("12345678-1234-5678-9abc-123456789abc").username("ada").phoneNumber("111")
                .address(new Address("s", "c", "st", "p", "co"))
                .dateOfBirth(LocalDate.of(1990, 1, 2)).jobTitle("CEO");
    }
}

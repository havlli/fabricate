package dev.havlli.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import dev.havlli.fabricate.model.Address;
import org.junit.jupiter.api.Test;

class AddressGenTest {

    private static Fabricate seeded() {
        return Fabricate.builder().seed(42L).build();
    }

    @Test
    void streetOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(AddressGen::street, AddressGen::street, Address::street,
                (fab, value) -> new Address(
                        value, fab.addresses().city(), fab.addresses().state(), fab.addresses().postalCode(), fab.addresses().country()));
    }

    @Test
    void cityOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(AddressGen::city, AddressGen::city, Address::city,
                (fab, value) -> new Address(
                        fab.addresses().street(), value, fab.addresses().state(), fab.addresses().postalCode(), fab.addresses().country()));
    }

    @Test
    void stateOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(AddressGen::state, AddressGen::state, Address::state,
                (fab, value) -> new Address(
                        fab.addresses().street(), fab.addresses().city(), value, fab.addresses().postalCode(), fab.addresses().country()));
    }

    @Test
    void postalCodeOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(AddressGen::postalCode, AddressGen::postalCode, Address::postalCode,
                (fab, value) -> new Address(
                        fab.addresses().street(), fab.addresses().city(), fab.addresses().state(), value, fab.addresses().country()));
    }

    @Test
    void countryOverridesAreImmutableLazyAndRejectNulls() {
        checkOverride(AddressGen::country, AddressGen::country, Address::country,
                (fab, value) -> new Address(
                        fab.addresses().street(), fab.addresses().city(), fab.addresses().state(), fab.addresses().postalCode(), value));
    }

    private static void checkOverride(
            BiFunction<AddressGen, String, AddressGen> fixed,
            BiFunction<AddressGen, Gen<? extends String>, AddressGen> generated,
            Function<Address, String> read,
            BiFunction<Fabricate, String, Address> expected) {
        Fabricate fab = seeded();
        Fabricate reference = seeded();
        AddressGen base = fab.address();
        AddressGen overridden = fixed.apply(base, "fixed");
        AtomicInteger calls = new AtomicInteger();
        AddressGen dynamic = generated.apply(base, fab.gen(() -> "draw-" + calls.incrementAndGet()));
        assertThat(overridden).isNotSameAs(base);
        assertThat(dynamic).isNotSameAs(base);
        assertThat(calls).hasValue(0);
        assertThat(base.get()).isEqualTo(reference.addresses().address());
        assertThat(overridden.get()).isEqualTo(expected.apply(reference, "fixed"));
        for (int i = 1; i <= 3; i++) {
            assertThat(dynamic.get()).isEqualTo(expected.apply(reference, "draw-" + i));
        }
        assertThat(calls).hasValue(3);
        AddressGen replaced = fixed.apply(overridden, "replacement");
        assertThat(read.apply(replaced.get())).isEqualTo("replacement");
        assertThat(read.apply(overridden.get())).isEqualTo("fixed");
        expected.apply(reference, "replacement");
        expected.apply(reference, "fixed");
        assertThat(fab.rng().nextInt(1_000_000)).isEqualTo(reference.rng().nextInt(1_000_000));
        assertThatThrownBy(() -> fixed.apply(base, null))
                .isInstanceOf(NullPointerException.class).hasMessage("value");
        assertThatThrownBy(() -> generated.apply(base, null))
                .isInstanceOf(NullPointerException.class).hasMessage("gen");
    }

    @Test
    void defaultsMatchLegacyDrawForDrawAcrossLocalesAndSeeds() {
        for (var locale : Fabricate.availableLocales()) {
            for (long seed : new long[]{0, 42, -7}) {
                Fabricate fab = Fabricate.builder().locale(locale).seed(seed).build();
                Fabricate reference = Fabricate.builder().locale(locale).seed(seed).build();
                AddressGen gen = fab.address();
                for (int i = 0; i < 20; i++) {
                    assertThat(gen.get()).isEqualTo(reference.addresses().address());
                    assertThat(fab.rng().nextInt(1_000_000)).isEqualTo(reference.rng().nextInt(1_000_000));
                }
            }
        }
    }

    @Test
    void supportsListsLazyStreamsAndUnique() {
        List<Address> addresses = seeded().address().country("Germany").list(10);
        assertThat(addresses).hasSize(10).allSatisfy(a -> assertThat(a.country()).isEqualTo("Germany"));
        assertThatThrownBy(() -> addresses.add(addresses.getFirst())).isInstanceOf(UnsupportedOperationException.class);
        Fabricate fab = seeded();
        AtomicInteger calls = new AtomicInteger();
        var stream = fab.address().city(fab.gen(() -> "city-" + calls.incrementAndGet())).stream();
        assertThat(calls).hasValue(0);
        assertThat(stream.limit(3).toList()).extracting(Address::city).containsExactly("city-1", "city-2", "city-3");
        AddressGen constant = fab.address().street("s").city("c").state("st").postalCode("p").country("co");
        Gen<Address> unique = constant.unique();
        assertThat(unique.get()).isEqualTo(constant.get());
        assertThatThrownBy(unique::get).isInstanceOf(UniqueExhaustedException.class);
        assertThat(constant.unique().get()).isEqualTo(constant.get());
    }

    @Test
    void allFixedFieldsConsumeNoRandomness() {
        Fabricate fab = seeded();
        fab.address().street("s").city("c").state("st").postalCode("p").country("co").list(5);
        assertThat(fab.rng().nextInt(1_000_000)).isEqualTo(seeded().rng().nextInt(1_000_000));
    }
}

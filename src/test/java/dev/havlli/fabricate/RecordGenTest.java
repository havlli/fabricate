package dev.havlli.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

class RecordGenTest {

    record Order(UUID id, String status, BigDecimal total) {}
    record Counts(int first, int second) {}
    record Nested(int first, Counts child, FieldBean bean) {}
    record NullableValue(@Nullable String value) {}
    record Primitives(boolean flag, byte b, short s, int i, long l, float f, double d, char c) {}
    record Assignable(Number number, CharSequence text, List<String> items) {}
    record ExactNames(String firstName, String first_name) {}

    public static class SetterBean {
        private String status;
        private int count;
        private int calls;

        public void setStatus(String status) { this.status = status; }
        public void setCount(int count) { this.count = count; }
        public void setCalls(int ignored) { calls++; }
    }

    public static class FieldBean {
        private String email;
        private int first;
        public static String shared = "unchanged";
        public final String constant = "unchanged";
    }

    public static class MixedNamesBean {
        private String firstName;
        private String first_name;
        public void setFirstName(String name) { firstName = name; }
    }

    interface NotABean {}
    abstract static class AbstractBean {}
    static class NoDefaultConstructor {
        NoDefaultConstructor(String ignored) {}
    }
    enum Choice { ONE }

    private static Fabricate seeded() {
        return Fabricate.builder().seed(42L).build();
    }

    @Test
    void fixedAndGenOverridesSupportTheBriefExample() {
        Fabricate fab = seeded();
        List<Order> orders = fab.of(Order.class)
                .with("status", fab.oneOf("OPEN", "PAID"))
                .with("total", BigDecimal.TEN)
                .list(1_000);

        assertThat(orders).hasSize(1_000).allSatisfy(order -> {
            assertThat(order.id()).isNotNull();
            assertThat(order.status()).isIn("OPEN", "PAID");
            assertThat(order.total()).isEqualTo(BigDecimal.TEN);
        });
    }

    @Test
    void supplierOverridesAreLazyAndInvokedForEachDraw() {
        AtomicInteger calls = new AtomicInteger();
        RecordGen<Counts> gen = seeded().of(Counts.class).with("first", calls::incrementAndGet);

        assertThat(calls).hasValue(0);
        assertThat(gen.list(3)).extracting(Counts::first).containsExactly(1, 2, 3);
        assertThat(calls).hasValue(3);
    }

    @Test
    void genOverridesAreLazyAndInvokedForEachDraw() {
        Fabricate fab = seeded();
        AtomicInteger calls = new AtomicInteger();
        RecordGen<Counts> gen = fab.of(Counts.class)
                .with("first", fab.gen(calls::incrementAndGet));

        assertThat(calls).hasValue(0);
        assertThat(gen.list(3)).extracting(Counts::first).containsExactly(1, 2, 3);
    }

    @Test
    void unknownNamesAreRejectedEagerlyForEveryOverload() {
        Fabricate fab = seeded();
        RecordGen<Order> gen = fab.of(Order.class);
        assertThatThrownBy(() -> gen.with("Status", "OPEN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Status").hasMessageContaining("valid names: [id, status, total]");
        assertThatThrownBy(() -> gen.with("missing", () -> "OPEN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("valid names: [id, status, total]");
        assertThatThrownBy(() -> gen.with("missing", fab.oneOf("OPEN")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("valid names: [id, status, total]");
    }

    @Test
    void namesAreExactEvenWhenHeuristicsNormalizeThem() {
        ExactNames value = seeded().of(ExactNames.class)
                .with("firstName", "camel").with("first_name", "snake").get();
        assertThat(value).isEqualTo(new ExactNames("camel", "snake"));
        assertThatThrownBy(() -> seeded().of(ExactNames.class).with("FIRST_NAME", "upper"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[firstName, first_name]");
    }

    @Test
    void fixedValuesAreCheckedEagerlyWithoutNumericCoercion() {
        RecordGen<Counts> gen = seeded().of(Counts.class);
        assertThatThrownBy(() -> gen.with("first", "wrong"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("first").hasMessageContaining("int").hasMessageContaining("java.lang.String");
        assertThatThrownBy(() -> gen.with("first", 1L))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("first");
        assertThatThrownBy(() -> gen.with("first", (Object) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("first").hasMessageContaining("null");
        assertThatThrownBy(() -> seeded().of(Order.class).with("total", "ten"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("total").hasMessageContaining("java.math.BigDecimal");
    }

    @Test
    void fixedValuesSupportEveryPrimitiveBox() {
        Primitives value = seeded().of(Primitives.class)
                .with("flag", true).with("b", (byte) 1).with("s", (short) 2)
                .with("i", 3).with("l", 4L).with("f", 5.0f).with("d", 6.0).with("c", 'x').get();
        assertThat(value).isEqualTo(new Primitives(true, (byte) 1, (short) 2, 3, 4L, 5.0f, 6.0, 'x'));
    }

    @Test
    void assignableSubtypesCanOverrideOtherwiseUnsupportedTargets() {
        List<String> items = List.of("item");
        Assignable value = seeded().of(Assignable.class).with("number", BigDecimal.TEN)
                .with("text", "hello").with("items", items).get();
        assertThat(value).isEqualTo(new Assignable(BigDecimal.TEN, "hello", items));
        assertThat(value.items()).isSameAs(items);
    }

    @Test
    void nullIsAllowedForReferenceTargetsWithEveryOverload() {
        Fabricate fab = seeded();
        RecordGen<NullableValue> gen = fab.of(NullableValue.class);
        assertThat(gen.with("value", (Object) null).get().value()).isNull();
        assertThat(gen.with("value", () -> null).get().value()).isNull();
        assertThat(gen.with("value", fab.gen(() -> null)).get().value()).isNull();
    }

    @Test
    void supplierAndGenResultsAreCheckedOnlyAtGenerationTime() {
        Fabricate fab = seeded();
        RecordGen<Counts> gen = fab.of(Counts.class);
        List<RecordGen<Counts>> invalid = List.of(
                gen.with("first", () -> "bad"), gen.with("first", fab.oneOf("bad")),
                gen.with("first", () -> null), gen.with("first", fab.gen(() -> null)));

        for (RecordGen<Counts> source : invalid) {
            assertThatThrownBy(source::get).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("first").hasMessageContaining("int");
        }
        AtomicInteger calls = new AtomicInteger();
        Supplier<Object> changing = () -> calls.incrementAndGet() == 1 ? 7 : "bad";
        RecordGen<Counts> changingGen = gen.with("first", changing);
        assertThat(changingGen.get().first()).isEqualTo(7);
        assertThatThrownBy(changingGen::get).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("first").hasMessageContaining("java.lang.String");
    }

    @Test
    void withReturnsNewConfigurationsAndReplacesAnEarlierOverride() {
        RecordGen<Counts> base = seeded().of(Counts.class);
        AtomicInteger oldCalls = new AtomicInteger();
        RecordGen<Counts> first = base.with("first", oldCalls::incrementAndGet);
        RecordGen<Counts> replaced = first.with("first", 17);
        RecordGen<Counts> branch = replaced.with("second", -23);

        assertThat(first).isNotSameAs(base);
        assertThat(replaced).isNotSameAs(first);
        assertThat(branch).isNotSameAs(replaced);
        assertThat(base.get()).isEqualTo(seeded().fill(Counts.class));
        assertThat(branch.get()).isEqualTo(new Counts(17, -23));
        Counts unchanged = replaced.get();
        assertThat(unchanged.first()).isEqualTo(17);
        assertThat(unchanged.second()).isBetween(0, 1_000_000);
        assertThat(oldCalls).hasValue(0);
        assertThat(first.get().first()).isEqualTo(1);
    }

    @Test
    void nestedRecordsAndPojosUseHeuristicsWithoutInheritingOverrides() {
        Nested value = seeded().of(Nested.class).with("first", -1).get();
        assertThat(value.first()).isEqualTo(-1);
        assertThat(value.child().first()).isBetween(0, 1_000_000);
        assertThat(value.bean().first).isBetween(0, 1_000_000);
        assertThat(value.bean().email).contains("@");
        assertThatThrownBy(() -> seeded().of(Nested.class).with("child.first", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("valid names: [first, child, bean]");
    }

    @Test
    void pojoOverridesUseSettersBeforeBackingFields() {
        Fabricate fab = seeded();
        AtomicInteger draws = new AtomicInteger();
        SetterBean value = fab.of(SetterBean.class).with("status", "OPEN")
                .with("count", draws::incrementAndGet).with("calls", fab.oneOf(99)).get();
        assertThat(value.status).isEqualTo("OPEN");
        assertThat(value.count).isEqualTo(1);
        assertThat(value.calls).isEqualTo(1);
        assertThat(draws).hasValue(1);
    }

    @Test
    void pojoOverridesUseWritableFieldsAndRejectStaticAndFinalFields() {
        FieldBean value = seeded().of(FieldBean.class)
                .with("email", "fixed@example.test").with("first", 5).get();
        assertThat(value.email).isEqualTo("fixed@example.test");
        assertThat(value.first).isEqualTo(5);
        for (String name : List.of("Email", "shared", "constant")) {
            assertThatThrownBy(() -> seeded().of(FieldBean.class).with(name, "bad"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("valid names: [email, first]");
        }
    }

    @Test
    void exactFieldOverrideIsNotSuppressedByADifferentlyNamedSetter() {
        MixedNamesBean value = seeded().of(MixedNamesBean.class)
                .with("firstName", "camel").with("first_name", "snake").get();
        assertThat(value.firstName).isEqualTo("camel");
        assertThat(value.first_name).isEqualTo("snake");
    }

    @Test
    void pojoTypeErrorsAreNotSwallowedByReflectionHandling() {
        Fabricate fab = seeded();
        assertThatThrownBy(() -> fab.of(SetterBean.class).with("count", "bad"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("count");
        assertThatThrownBy(() -> fab.of(FieldBean.class).with("first", (Object) null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("first");
        RecordGen<SetterBean> setter = fab.of(SetterBean.class).with("count", () -> "bad");
        RecordGen<FieldBean> field = fab.of(FieldBean.class).with("first", fab.gen(() -> null));
        assertThatThrownBy(setter::get).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("count").hasMessageContaining("int");
        assertThatThrownBy(field::get).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("first").hasMessageContaining("null");
    }

    @Test
    void genCompositionWorks() {
        AtomicInteger calls = new AtomicInteger();
        RecordGen<Counts> gen = seeded().of(Counts.class)
                .with("first", calls::incrementAndGet).with("second", 0);
        Gen<Counts> asGen = gen;
        assertThat(asGen.list(3)).extracting(Counts::first).containsExactly(1, 2, 3);
        assertThat(gen.unique().set(5)).extracting(Counts::first).containsExactly(4, 5, 6, 7, 8);
        try (var stream = gen.stream()) {
            assertThat(calls).hasValue(8);
            assertThat(stream.limit(3).map(Counts::first).toList()).containsExactly(9, 10, 11);
        }
        assertThat(gen.map(Counts::first).get()).isEqualTo(12);
    }

    @Test
    void uniqueUsesRecordEquality() {
        RecordGen<Counts> constant = seeded().of(Counts.class).with("first", 1).with("second", 2);
        assertThatThrownBy(() -> constant.unique(2).set(2))
                .isInstanceOf(UniqueExhaustedException.class);
    }

    @Test
    void overriddenGenerationIsReproducibleUnderTheSameSeed() {
        Fabricate a = seeded();
        Fabricate b = seeded();
        RecordGen<Order> first = a.of(Order.class).with("status", a.oneOf("OPEN", "PAID"))
                .with("total", () -> BigDecimal.TEN);
        RecordGen<Order> second = b.of(Order.class).with("status", b.oneOf("OPEN", "PAID"))
                .with("total", () -> BigDecimal.TEN);
        assertThat(first.list(50)).isEqualTo(second.list(50));
    }

    @Test
    void noOverridesMatchFillDrawForDrawForRecordsAndPojos() {
        Fabricate generated = seeded();
        Fabricate filled = seeded();
        RecordGen<Order> orders = generated.of(Order.class);
        RecordGen<Nested> nested = generated.of(Nested.class);
        RecordGen<SetterBean> beans = generated.of(SetterBean.class);
        for (int i = 0; i < 30; i++) {
            assertThat(orders.get()).isEqualTo(filled.fill(Order.class));
            assertThat(nested.get()).usingRecursiveComparison().isEqualTo(filled.fill(Nested.class));
            assertThat(beans.get()).usingRecursiveComparison().isEqualTo(filled.fill(SetterBean.class));
        }
        assertThat(generated.rng().delegate().nextLong()).isEqualTo(filled.rng().delegate().nextLong());
    }

    @Test
    void overriddenComponentsConsumeNoHeuristicRandomDraws() {
        Fabricate generated = seeded();
        Fabricate expected = seeded();
        RecordGen<Counts> gen = generated.of(Counts.class).with("first", -1);
        for (int i = 0; i < 10; i++) {
            assertThat(gen.get()).isEqualTo(new Counts(-1, expected.number().between(0, 1_000_000)));
        }
        assertThat(generated.rng().delegate().nextLong()).isEqualTo(expected.rng().delegate().nextLong());
    }

    @Test
    void ofRejectsUnfillableTypesImmediately() {
        for (Class<?> type : List.of(String.class, int.class, int[].class, NotABean.class,
                AbstractBean.class, NoDefaultConstructor.class, Choice.class)) {
            assertThatThrownBy(() -> seeded().of(type)).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(type.getName());
        }
    }

    @Test
    void nullConfigurationArgumentsFailEagerly() {
        Fabricate fab = seeded();
        assertThatThrownBy(() -> fab.of(null)).isInstanceOf(NullPointerException.class);
        RecordGen<Counts> gen = fab.of(Counts.class);
        assertThatThrownBy(() -> gen.with(null, 1)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> gen.with("first", (Supplier<?>) null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> gen.with("first", (Gen<?>) null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @SuppressWarnings("removal")
    void deprecatedBeanFillerStillDelegatesWithIdenticalBehavior() {
        BeanFiller filler = new BeanFiller(seeded());
        Fabricate fab = seeded();
        for (int i = 0; i < 10; i++) {
            assertThat(filler.fill(Order.class)).isEqualTo(fab.fill(Order.class));
            assertThat(filler.fill(SetterBean.class)).usingRecursiveComparison().isEqualTo(fab.fill(SetterBean.class));
        }
        assertThatThrownBy(() -> filler.fill(String.class))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("record");
    }
}

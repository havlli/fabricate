package dev.havlli.fabricate.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.model.Address;
import dev.havlli.fabricate.model.Person;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class FabricateExtensionTest {

    record Order(String product, int quantity) {}

    record Envelope<T extends Customer>(String firstName, T value) {}

    static class Customer {
        String firstName;
        int age;
    }

    @Test
    void fabricatedIsARuntimeParameterAnnotationWithUnsetSize() throws Exception {
        assertThat(Fabricated.class.getAnnotation(Retention.class).value()).isEqualTo(RetentionPolicy.RUNTIME);
        assertThat(Fabricated.class.getAnnotation(Target.class).value()).containsExactly(ElementType.PARAMETER);
        assertThat(Fabricated.class.getMethod("size").getDefaultValue()).isEqualTo(-1);
    }

    @Nested
    @FabricateTest(seed = 42L, locale = "de")
    class WithFabricatedParameters {

        @Test
        void resolvesParameterizedRecordsUsingTheSameClassBasedFiller(@Fabricated Envelope<Customer> envelope) {
            Fabricate expected = Fabricate.builder().seed(42L).locale(Locale.GERMAN).build();
            assertThat(envelope).usingRecursiveComparison().isEqualTo(expected.of(Envelope.class).get());
            assertThat(envelope.firstName()).isNotBlank();
        }

        @Test
        void resolvesRecordsAndPojos(@Fabricated Order order, @Fabricated Customer customer, Fabricate fab) {
            Fabricate expected = Fabricate.builder().seed(42L).locale(Locale.GERMAN).build();
            assertThat(order).isEqualTo(expected.of(Order.class).get());
            assertThat(customer).usingRecursiveComparison().isEqualTo(expected.of(Customer.class).get());
            assertThat(customer.firstName).isNotBlank();
            assertThat(fab.locale().locale()).isEqualTo(Locale.GERMAN);
            // Resolution must consume the same per-test RNG that the unannotated Fabricate exposes.
            assertThat(fab.names().fullName()).isEqualTo(expected.names().fullName());
        }

        @Test
        void resolvesPeopleAndAddresses(@Fabricated Person person, @Fabricated Address address) {
            Fabricate expected = Fabricate.builder().seed(42L).locale(Locale.GERMAN).build();
            assertThat(person).isEqualTo(expected.person().get());
            assertThat(address).isEqualTo(expected.address().get());
        }

        @Test
        void resolvesLists(@Fabricated(size = 3) List<Order> orders,
                           @Fabricated(size = 2) List<Customer> customers,
                           @Fabricated(size = 4) List<Person> people,
                           @Fabricated(size = 1) List<Address> addresses) {
            Fabricate expected = Fabricate.builder().seed(42L).locale(Locale.GERMAN).build();
            assertThat(orders).hasSize(3).isEqualTo(expected.of(Order.class).list(3));
            assertThat(customers).hasSize(2).usingRecursiveComparison().isEqualTo(expected.of(Customer.class).list(2));
            assertThat(people).hasSize(4).isEqualTo(expected.person().list(4));
            assertThat(addresses).hasSize(1).isEqualTo(expected.address().list(1));
        }

        @Test
        void resolvesEmptyListsWithoutConsumingRandomValues(@Fabricated(size = 0) List<Order> orders,
                                                            @Fabricated(size = 0) List<Customer> customers,
                                                            @Fabricated(size = 0) List<Person> people,
                                                            @Fabricated(size = 0) List<Address> addresses,
                                                            Fabricate fab) {
            assertThat(orders).isEmpty();
            assertThat(customers).isEmpty();
            assertThat(people).isEmpty();
            assertThat(addresses).isEmpty();
            Fabricate expected = Fabricate.builder().seed(42L).locale(Locale.GERMAN).build();
            assertThat(fab.names().fullName()).isEqualTo(expected.names().fullName());
        }

        @Test
        @Seed(99L)
        void methodSeedOverridesClassSeed(@Fabricated Person person, @Fabricated Order order,
                                         @Fabricated(size = 2) List<Address> addresses) {
            Fabricate expected = Fabricate.builder().seed(99L).locale(Locale.GERMAN).build();
            assertThat(person).isEqualTo(expected.person().get());
            assertThat(order).isEqualTo(expected.of(Order.class).get());
            assertThat(addresses).isEqualTo(expected.address().list(2));
        }
    }

    @Nested
    @FabricateTest(seed = 42L)
    class InvalidFabricatedParameters {
        private ExtensionContext context;

        @RegisterExtension
        final BeforeEachCallback captureContext = current -> context = current;

        static Stream<Parameter> invalidParameters() {
            return Arrays.stream(UnsupportedParameters.class.getDeclaredMethods())
                    .filter(method -> !method.isSynthetic())
                    .map(method -> method.getParameters()[0]);
        }

        @ParameterizedTest
        @MethodSource("invalidParameters")
        void reportsTheUnsupportedParameterType(Parameter parameter) {
            ParameterContext parameterContext = parameterContext(parameter);
            FabricateExtension extension = new FabricateExtension();
            assertThat(extension.supportsParameter(parameterContext, context)).isTrue();
            var failure = assertThatThrownBy(() -> extension.resolveParameter(parameterContext, context))
                    .isInstanceOf(ParameterResolutionException.class)
                    .hasMessageContaining("@Fabricated")
                    .hasMessageContaining(parameter.getParameterizedType().getTypeName());
            if (parameter.getAnnotation(Fabricated.class).size() < 0 && parameter.getType() == List.class) {
                failure.hasMessageContaining("size is required for lists");
            }
        }

        @Test
        void doesNotClaimUnannotatedRecords() throws Exception {
            Parameter parameter = UnannotatedParameters.class.getDeclaredMethod("record", Order.class)
                    .getParameters()[0];
            assertThat(new FabricateExtension().supportsParameter(parameterContext(parameter), context)).isFalse();
        }
    }

    private static ParameterContext parameterContext(Parameter parameter) {
        return new ParameterContext() {
            @Override
            public Parameter getParameter() {
                return parameter;
            }

            @Override
            public int getIndex() {
                return 0;
            }

            @Override
            public Optional<Object> getTarget() {
                return Optional.empty();
            }
        };
    }

    private static class UnsupportedParameters {
        void missingSize(@Fabricated List<Person> value) {}
        void negativeSize(@Fabricated(size = -2) List<Address> value) {}
        @SuppressWarnings("rawtypes") // Deliberately invalid parameter shape.
        void rawList(@Fabricated(size = 1) List value) {}
        void wildcard(@Fabricated(size = 1) List<?> value) {}
        void boundedWildcard(@Fabricated(size = 1) List<? extends Person> value) {}
        void nestedList(@Fabricated(size = 1) List<List<Person>> value) {}
        <T> void typeVariable(@Fabricated(size = 1) List<T> value) {}
        void unsupportedElement(@Fabricated(size = 1) List<String> value) {}
        void unsupportedEmptyList(@Fabricated(size = 0) List<String> value) {}
        void fabricateElement(@Fabricated(size = 1) List<Fabricate> value) {}
        void fabricate(@Fabricated Fabricate value) {}
        void string(@Fabricated String value) {}
        void primitive(@Fabricated int value) {}
        void array(@Fabricated Person[] value) {}
        void set(@Fabricated(size = 1) Set<Person> value) {}
    }

    private static class UnannotatedParameters {
        void record(Order value) {}
    }

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

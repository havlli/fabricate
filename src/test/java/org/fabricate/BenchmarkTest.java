package org.fabricate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Supplier;
import org.fabricate.model.Person;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Lightweight throughput smoke benchmark — not JMH-grade, but enough to
 * surface order-of-magnitude regressions and to give consumers a feel for
 * the cost of each provider.
 *
 * <p>Tagged {@code "bench"} so it can be run on demand:
 * <pre>{@code mvn test -Dgroups=bench}</pre>
 *
 * <p>Numbers are printed; the assertion only verifies that each provider
 * sustains at least 10 k ops/sec, which is several orders of magnitude
 * below what we actually see and is only there to catch a cataclysmic
 * regression (e.g. accidentally adding a network call).
 */
@Tag("bench")
class BenchmarkTest {

    private static final long WARMUP_NANOS = 200_000_000L; // 200 ms
    private static final long MEASURE_NANOS = 500_000_000L; // 500 ms
    private static final double MIN_OPS_PER_SEC = 10_000;

    @Test
    void benchmarkCommonProviders() {
        Fabricate fab = Fabricate.builder().seed(42L).build();

        record Bench(String name, Supplier<?> body) {}
        java.util.List<Bench> benches = java.util.List.of(
                new Bench("names.firstName", fab.names()::firstName),
                new Bench("emails.email", fab.emails()::email),
                new Bench("addresses.address", fab.addresses()::address),
                new Bench("internet.url", fab.internet()::url),
                new Bench("internet.ipv4", fab.internet()::ipv4),
                new Bench("finance.iban", fab.finance()::iban),
                new Bench("finance.creditCard", fab.finance()::creditCard),
                new Bench("commerce.isbn13", fab.commerce()::isbn13),
                new Bench("devops.semver", fab.devops()::semver),
                new Bench("identities.uuid", fab.identities()::uuid),
                new Bench("persons.person", (Supplier<Person>) fab.persons()::person),
                new Bench("fill(Person.class)", () -> fab.fill(Person.class))
        );

        System.out.println();
        System.out.println("== fabricate throughput ==");
        System.out.printf("%-26s %14s%n", "provider", "ops/sec");
        System.out.println("-".repeat(42));

        for (Bench b : benches) {
            double opsPerSec = measure(b.body);
            System.out.printf("%-26s %,14.0f%n", b.name, opsPerSec);
            assertThat(opsPerSec)
                    .as(b.name + " throughput")
                    .isGreaterThan(MIN_OPS_PER_SEC);
        }
    }

    private static double measure(Supplier<?> body) {
        long warmupEnd = System.nanoTime() + WARMUP_NANOS;
        while (System.nanoTime() < warmupEnd) {
            body.get();
        }

        long start = System.nanoTime();
        long deadline = start + MEASURE_NANOS;
        long ops = 0;
        while (System.nanoTime() < deadline) {
            for (int i = 0; i < 64; i++) {
                body.get();
            }
            ops += 64;
        }
        long elapsed = System.nanoTime() - start;
        return ops / (elapsed / 1_000_000_000.0);
    }
}

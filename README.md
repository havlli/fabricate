# fabricate

[![Build](https://github.com/havlli/fabricate/actions/workflows/build.yml/badge.svg)](https://github.com/havlli/fabricate/actions/workflows/build.yml)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Lightweight, zero-runtime-dependency Java library for generating realistic
random data — names, emails, addresses, dates, identities, full `Person`
records, and arbitrary user-defined records.

Built for tests, demos, and load-data fixtures. Java 21+. JPMS-ready.

## Features

- **Reproducible** — seed once, get the same data every run. Built on JEP 356
  `RandomGenerator` (`L64X128MixRandom`); the algorithm is JDK-stable, so
  seeded sequences survive minor JDK upgrades.
- **Locale-aware** — English and Chinese pools out of the box, plus an SPI
  (`LocaleProvider`) for adding your own without forking.
- **Stream APIs** — `fab.persons().stream()` for lazy unbounded sequences,
  `fab.persons().list(n)` for bounded eager generation.
- **Unique decorator** — `Unique.of(fab.emails()::email)` produces distinct
  values with a bounded retry budget; finite pools fail fast and explicitly.
- **Builder overrides** — fix one field, randomize the rest:
  `fab.persons().builder().firstName("Ada").build()`.
- **Bean fill** — `fab.fill(MyRecord.class)` reflectively populates any
  record type, using component names as hints.
- **JUnit 5 extension** — `@FabricateTest` injects a fresh, seeded
  `Fabricate` (and `Person`/`Address`) directly into test parameters.
- **No runtime dependencies** — JSpecify and JUnit Jupiter are both
  declared `optional`/`requires static`; consumers pay for what they use.

## Quick start

```xml
<dependency>
    <groupId>io.github.havlli</groupId>
    <artifactId>fabricate</artifactId>
    <version>0.1.0</version>
</dependency>
```

```java
Fabricate fab = Fabricate.builder().seed(42L).build();

Person person     = fab.persons().person();
String email      = fab.emails().email();
List<Person> bulk = fab.persons().list(1_000);

// override one field, randomize the rest
Person ceo = fab.persons().builder().jobTitle("CEO").build();

// distinct values from a finite pool
Unique<String> distinctEmails = Unique.of(fab.emails()::email);
List<String> noDuplicates = distinctEmails.list(50);

// fill any record by reflection
record Customer(String firstName, String email, LocalDate dateOfBirth) {}
Customer c = fab.fill(Customer.class);
```

## JUnit 5 extension

```java
@FabricateTest(seed = 42L)
class UserServiceTest {

    @Test
    void importsUsers(Fabricate fab) {
        List<Person> users = fab.persons().list(100);
        // ...
    }

    @Test
    @Seed(7L)
    void regressionForBugX(Person person) {
        // pinned to seed 7L regardless of the class-level seed
    }
}
```

## Locales

```java
Fabricate fab = Fabricate.builder()
        .locale(Locale.CHINESE)
        .seed(1L)
        .build();

System.out.println(fab.names().fullName()); // e.g. 王伟
```

Add your own locale by implementing `LocaleData` and registering a
`LocaleProvider` via `META-INF/services/org.fabricate.spi.LocaleProvider`.

## Reproducibility guarantees

`Fabricate` instances are immutable and thread-confined. Two instances
built with the same seed produce identical sequences across every
provider as long as the calls are made in the same order. For
parallelism, build one `Fabricate` per worker rather than sharing.

`Identities.uuid()` is seedable (does not call `UUID.randomUUID()`), so
seeded test runs produce stable UUIDs. `Passwords` deliberately uses
`SecureRandom` and is **not** seeded — generated passwords are never
reproducible by design.

## Modules and JPMS

```java
module my.app {
    requires org.fabricate;
    // optional, only if you use the JUnit extension:
    requires org.junit.jupiter.api;

    // expose your record types so BeanFiller can construct them
    opens my.app.dto to org.fabricate;
}
```

## License

Apache License 2.0 — see [LICENSE](LICENSE).

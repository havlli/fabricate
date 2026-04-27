# fabricate

[![Build](https://github.com/havlli/fabricate/actions/workflows/build.yml/badge.svg)](https://github.com/havlli/fabricate/actions/workflows/build.yml)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Lightweight, zero-runtime-dependency Java library for generating realistic
random data — names, emails, addresses, dates, full `Person` records,
credit cards, IBANs, URLs, file paths, semvers, git SHAs, and any record
type you point it at.

Built for tests, demos, fixtures, and corpus generation. Java 21+. JPMS-ready.

## Features

- **Zero-setup static facade** — `Fake.firstName()`, `Fake.iban()`,
  `Fake.creditCard()`, `Fake.gitSha()`. Default seed is `0L` so quick
  scripts are reproducible without any setup.
- **Realistic data** — Luhn-valid credit cards across 6 brands,
  IBANs with mod-97 check digits for 32 countries, EAN-13/ISBN-13 with
  valid GS1 checksums, ~20 curated User-Agent strings, ~50 file extensions
  with matching MIME types.
- **Reproducible** — seed once, get the same data every run. Built on JEP 356
  `RandomGenerator` (`L64X128MixRandom`); the algorithm is JDK-stable, so
  seeded sequences survive minor JDK upgrades.
- **Locale-aware** — built-in pools for English, Chinese, Russian (Cyrillic),
  Japanese (CJK + Kana), Korean (Hangul), Arabic (RTL), and German (Latin
  with umlauts). Plus an SPI (`LocaleProvider`) for adding your own.
- **One-line corpus dumpers** — `Sinks.toCsv(rows, path)`,
  `Sinks.toJsonLines(rows, path)`, `Sinks.toSqlInserts(rows, table, path)`
  for any list of records.
- **Stream APIs** — `fab.persons().stream()` for lazy unbounded sequences,
  `fab.persons().list(n)` for bounded eager generation, `fab.repeat(n, …)`
  for arbitrary suppliers.
- **Sequences** — `Sequence.counter("USR-", 1)` and
  `Sequence.cycle("admin", "user", "guest")` for deterministic IDs and
  round-robin values.
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

## Building from source

The repo pins JDK and Maven versions via [mise](https://mise.jdx.dev):

```bash
mise install   # one-time: installs Temurin 21 + Maven 3.9.9
mise exec -- mvn verify
```

Or use any JDK 21+ and Maven 3.9+ already on your `PATH`.

## Quick start

```xml
<dependency>
    <groupId>io.github.havlli</groupId>
    <artifactId>fabricate</artifactId>
    <version>0.1.0</version>
</dependency>
```

```java
// Zero-setup facade with default seed (0L) for quick scripts
String name = Fake.fullName();
String iban = Fake.iban("DE");
String card = Fake.creditCard("VISA");
String env  = Fake.environment();

// Or build a Fabricate when you need control
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

// dump 1k rows to a CSV/JSONL/SQL fixture
Sinks.toCsv(bulk, Path.of("people.csv"));
Sinks.toJsonLines(bulk, Path.of("people.jsonl"));
Sinks.toSqlInserts(bulk, "person", Path.of("people.sql"));
```

## What you can generate

| Provider              | Examples                                                      |
|-----------------------|---------------------------------------------------------------|
| `numbers` / `booleans`| `intBetween`, `gaussian`, `withProbability`                   |
| `texts`               | lorem `word`/`sentence`/`paragraph`, `slug`, `hex`, `alphanumeric` |
| `names` / `emails` / `phones` / `addresses` | localised, full `Person` builder           |
| `internet`            | `url`, `ipv4`/`ipv6`, `macAddress`, `userAgent`, `port`       |
| `files`               | `fileName`, `path`, `mimeType`, `fileSizeBytes` (log-uniform) |
| `finance`             | Luhn-valid `creditCard`, `cvv`, mod-97 `iban`, `currencyCode`, `money` |
| `commerce`            | `productName`, `sku`, `isbn13`, `colorName`/`colorHex`        |
| `devops`              | `semver`, `gitSha`, `httpStatus`, `branchName`, `dockerImage`, `cloudRegion` |
| `dates`               | `between`, `recent`/`future`/`past`, `instantBetween`, `weekday`, `month` |
| `Sequence`            | monotonic `Counter`, cyclic `Cycle`                           |
| `Sinks`               | `toCsv`, `toJsonLines`, `toSqlInserts` over any record stream |

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

Built-in locales cover the major writing systems you'll need to test:

| Locale            | Script                                          |
|-------------------|-------------------------------------------------|
| `Locale.ENGLISH`  | Latin (ASCII)                                   |
| `Locale.GERMAN`   | Latin with umlauts and ß                        |
| `Locale.FRENCH`   | Latin with accents and ç                        |
| `Locale.of("es")` | Latin (Spanish — Spain + LatAm pools)           |
| `Locale.of("pt")` | Latin (Portuguese — Portugal + Brazil pools)    |
| `Locale.CHINESE`  | CJK ideographs                                  |
| `Locale.JAPANESE` | CJK + Hiragana + Katakana                       |
| `Locale.KOREAN`   | Hangul                                          |
| `Locale.of("ru")` | Cyrillic                                        |
| `Locale.of("ar")` | Arabic (RTL)                                    |
| `Locale.of("hi")` | Devanagari (Hindi)                              |

```java
Fabricate fab = Fabricate.builder()
        .locale(Locale.of("ar"))
        .seed(1L)
        .build();

System.out.println(fab.names().fullName()); // e.g. محمد العلي
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

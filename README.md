# fabricate

[![Build](https://github.com/havlli/fabricate/actions/workflows/build.yml/badge.svg)](https://github.com/havlli/fabricate/actions/workflows/build.yml)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Lightweight, zero-runtime-dependency Java library for generating realistic
random data — names, emails, addresses, dates, full `Person` records,
credit cards, IBANs, URLs, file paths, semvers, git SHAs, and any record
type you point it at.

Built for tests, demos, fixtures, and corpus generation. Java 21+. JPMS-ready.

## Features

- **Zero-setup static facade** — `Fake.first()`, `Fake.iban()`,
  `Fake.creditCard()`, `Fake.gitSha()`. Default seed is `0L` so scripts and demos
  are reproducible without setup. Treat it as read-only; build a local
  `Fabricate` for tests and production code.
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
- **One-line corpus dumpers** — `Sinks.csv().write(rows, path)`,
  `Sinks.jsonLines().write(rows, path)`, `Sinks.sql(table).write(rows, path)`
  for any iterable or stream of records, with Path, Writer, and string output.
- **Stream APIs** — `fab.person().stream()` for lazy unbounded sequences,
  `fab.person().list(n)` for bounded eager generation, `fab.gen(supplier).list(n)`
  for arbitrary suppliers.
- **Sequences** — `Sequence.counter("USR-", 1)` and
  `Sequence.cycle("admin", "user", "guest")` for deterministic IDs and
  round-robin values.
- **Unique decorator** — `Unique.of(fab.internet()::email)` produces distinct
  values with a bounded retry budget; finite pools fail fast and explicitly.
- **Fluent overrides** — fix one field, randomize the rest:
  `fab.person().firstName("Ada").get()`.
- **Records and beans** — `fab.of(MyRecord.class).get()` reflectively populates
  records and POJOs, with fluent overrides and component names as hints.
- **JUnit 5 extension** — `@FabricateTest` injects a fresh, seeded
  `Fabricate` directly into test parameters; `@Fabricated` also injects
  records, POJOs, and sized lists.
- **No runtime dependencies** — JSpecify and JUnit Jupiter are both
  declared optional in Maven and `requires static transitive` in JPMS.
  Modular compilation needs both on the module path; see [Modules and JPMS](#modules-and-jpms).

## Breaking changes in 0.2.0

0.2.0 moves to singular fluent generators, JDK `RandomGenerator`, and direct
`LocaleData` configuration. Implementation packages are no longer exported.
Some older helpers remain deprecated for removal, including the global `Fake`
mutators. See [MIGRATION-0.2.md](MIGRATION-0.2.md) for every replacement and
before/after examples.

## Compatibility policy

0.2.0 is the pre-1.0 breaking API transition. From 1.0, public packages only
accrete: existing APIs stay compatible, and an incompatible change requires a
new package. Provider interfaces obtained from `Fabricate` may gain methods;
applications must not implement them. The locale SPI is intended for
third-party implementations.

## Building from source

The repo pins JDK and Maven versions via [mise](https://mise.jdx.dev):

```bash
mise install   # one-time: installs Temurin 21 + Maven 3.9.9
mise exec -- mvn verify
```

Or use any JDK 21+ and Maven 3.9+ already on your `PATH`.

## Quick start

These examples describe the 0.2.0 API in this source tree. The Maven version
remains `0.1.0` during development; the coordinates below match the locally
built artifact and do not announce a 0.2.0 release.

```xml
<dependency>
    <groupId>dev.havlli</groupId>
    <artifactId>fabricate</artifactId>
    <version>0.1.0</version>
    <!-- optional: <scope>test</scope> when used only in tests -->
</dependency>
```

Gradle (Kotlin DSL):

```kotlin
testImplementation("dev.havlli:fabricate:0.1.0")
```

Gradle (Groovy):

```groovy
testImplementation 'dev.havlli:fabricate:0.1.0'
```

```java
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import dev.havlli.fabricate.Fake;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.Unique;
import dev.havlli.fabricate.model.Address;
import dev.havlli.fabricate.model.Person;
import dev.havlli.fabricate.sink.Sinks;

// Zero-setup facade with default seed (0L) for quick scripts
String name = Fake.full();
String iban = Fake.iban("DE");
String card = Fake.creditCard("VISA");
String env  = Fake.environment();

// Or build a Fabricate when you need control
Fabricate fab = Fabricate.builder().seed(42L).build();

String fullName   = fab.name().full();
int score         = fab.number().between(1, 500);
String title      = fab.job().title();
Person person     = fab.person().get();
String email      = fab.internet().email();
List<Person> bulk = fab.person().list(1_000);

// override one field, randomize the rest
Person ceo = fab.person().jobTitle("CEO").get();
List<Person> berliners = fab.person().address(fab.address().city("Berlin")).list(5);
List<Address> de = fab.address().country("Germany").list(10);

// distinct values from a finite pool
Unique<String> distinctEmails = Unique.of(fab.internet()::email);
List<String> noDuplicates = distinctEmails.list(50);

// fill any record by reflection
record Customer(String firstName, String email, LocalDate dateOfBirth) {}
Customer c = fab.of(Customer.class).get();

// dump 1k rows to a CSV/JSONL/SQL fixture
Sinks.csv().write(bulk, Path.of("people.csv"));
Sinks.jsonLines().write(bulk, Path.of("people.jsonl"));
Sinks.sql("person").batchSize(500).write(bulk, Path.of("people.sql"));

// stream directly to JSON Lines without collecting the records first
Sinks.jsonLines().write(fab.person().stream().limit(10_000), Path.of("corpus.jsonl"));
String csv = Sinks.csv().writeToString(bulk);
```

### Fluent generators

`fab.person()` and `fab.address()` provide immutable field overrides: use a fixed
value or a `Gen` to draw that field for each record. Email and username follow the
generated names unless overridden. Overrides skip the field's default random
draws; supplied generators may consume random values themselves.

Wrap any supplier with `fab.gen(...)`, or use `fab.oneOf(...)` for a pool of choices:

```java
import java.util.List;
import java.util.Optional;
import dev.havlli.fabricate.Fabricate;

Fabricate fab = Fabricate.builder().seed(42L).build();
List<String> names = fab.gen(fab.name()::full).unique().list(100);
List<String> statuses = fab.oneOf("PAID", "OPEN").stream().limit(20).toList();
List<Optional<String>> emails = fab.gen(fab.internet()::email).optional(0.8).list(10);
```

`Gen<T>` also offers `get()`, `map(fn)`, `orNull(probability)`, and `set(count)`.
Lists and sets are immutable; sets preserve insertion order. Each `unique()`
wrapper keeps its own seen-set and throws `UniqueExhaustedException` if its
retry budget runs out (default 100 attempts per value; override with `unique(budget)`).
Generators share their owning `Fabricate`'s RNG and are intended for use on one thread.

#### Records and beans

Use `fab.of(...)` to generate records or POJOs with a no-arg constructor:

```java
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import dev.havlli.fabricate.Fabricate;

Fabricate fab = Fabricate.builder().seed(42L).build();
record Order(UUID id, String status, BigDecimal total) {}

List<Order> orders = fab.of(Order.class)
        .with("status", fab.oneOf("OPEN", "PAID"))
        .with("total", BigDecimal.TEN)
        .list(1_000);
```

`RecordGen<T>` is a `Gen<T>`, so `unique()`, `stream()`, and `map()` work too.
Each `with` returns a new generator and accepts a fixed value, a supplier, or a
`Gen`. Names match exactly; a later override replaces the earlier one. Use
`(Object) null` for a fixed null on a non-primitive target. Overrides affect only
the top-level object; nested records and beans still use heuristics. Overridden
properties skip random generation, changing the RNG sequence of later properties.
`fab.fill(Type.class)` remains available for a single value without overrides.

## What you can generate

| Accessor | Examples |
|---|---|
| `name()` | `first()`, `last()`, `full()`, `list(count)` |
| `phone()` | `number()` |
| `person()` / `address()` | `get()`, `list(count)`, immutable field overrides |
| `dateOfBirth()` / `job()` / `identity()` | `get(minAge, maxAge)`, `title()`, `uuid()` |
| `number()` / `bool()` | `between(min, max)`, `gaussian(mean, stddev)`, `get()`, `withProbability(p)` |
| `text()` | `word()`, `sentence()`, `paragraph()`, `slug()`, `hex(length)`, `alphanumeric(length)` |
| `password()` | `get()`, `get(length)`, `get(length, alphabet)` |
| `internet()` | `email()`, `url()`, `ipv4()`, `ipv6()`, `macAddress()`, `userAgent()`, `port()` |
| `geo()` | `latitude()`, `longitude()`, `coordinate()`, `timezone()` |
| `file()` | `name()`, `path()`, `mimeType()`, `sizeBytes()` |
| `finance()` | `creditCard()`, `cvv()`, `iban()`, `currencyCode()`, `money()` |
| `commerce()` | `productName()`, `sku()`, `isbn13()`, `colorName()`, `colorHex()` |
| `devops()` | `semver()`, `gitSha()`, `httpStatus()`, `branchName()`, `dockerImage()`, `cloudRegion()` |
| `date()` | `between(from, to)`, `recent(days)`, `future(days)`, `past(years)`, `instantBetween(from, to)` |

Obtain generator interfaces from `Fabricate`; applications must not implement
them. `name().list(count)` draws full names, and `identity().list(count)` draws
UUIDs. For email lists and streams use `fab.gen(fab.internet()::email)`.
Read address components from a generated `Address` record.

## JUnit 5 extension

```java
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.junit.FabricateTest;
import dev.havlli.fabricate.junit.Fabricated;
import dev.havlli.fabricate.junit.Seed;
import dev.havlli.fabricate.model.Person;

@FabricateTest(seed = 42L, locale = "de")
class UserServiceTest {
    record Order(String product, int quantity) {}

    @Test
    void importsUsers(@Fabricated Order order,
                      @Fabricated(size = 10) List<Person> users, Fabricate fab) {
        // ...
    }

    @Test
    @Seed(7L)
    void regressionForBugX(@Fabricated Person person) {
        // pinned to seed 7L regardless of the class-level seed
    }
}
```

`@Fabricated` uses the per-test `Fabricate` instance, including the class seed
and locale and any method-level `@Seed` override. `Person` and `Address` use
their dedicated generators; other records and POJOs with a no-arg constructor
use `fab.of(type)` and its usual name and type heuristics. `List<T>` requires a
concrete supported element class and an explicit `size >= 0`; zero produces an
empty list. Raw lists, wildcards, and unsupported types are rejected.
Unannotated `Fabricate`, `Person`, and `Address` parameters remain supported;
leave `Fabricate` itself unannotated.

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
import java.util.Locale;

import dev.havlli.fabricate.Fabricate;

Fabricate fab = Fabricate.builder()
        .locale(Locale.of("ar"))
        .seed(1L)
        .build();

System.out.println(fab.name().full()); // e.g. محمد العلي
```

Add your own locale by implementing `LocaleData` and registering a
`LocaleProvider` via `META-INF/services/dev.havlli.fabricate.spi.LocaleProvider`
on the classpath, or `provides dev.havlli.fabricate.spi.LocaleProvider with ...`
in your module descriptor. For an individual instance, pass your data directly
with `locale(LocaleData)`. The last `locale(...)` call wins. For example, this
reuses data selected through the public API; substitute your own `LocaleData`
implementation for custom pools:

```java
import java.util.Locale;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.spi.LocaleData;

LocaleData data = Fabricate.builder().locale(Locale.GERMAN).build().locale();
Fabricate fab = Fabricate.builder().locale(data).seed(42L).build();
String name = fab.name().full();
```

## Reproducibility guarantees

`Fabricate` configuration is immutable, while its random sequence advances on
each draw. Instances are thread-confined. Two instances with the same seed,
locale, and call order reproduce seeded values. Date helpers relative to today
also depend on the clock. For parallelism, build one `Fabricate` per worker.

`identity().uuid()` is seedable (does not call `UUID.randomUUID()`), so
seeded test runs produce stable UUIDs. `password()` deliberately uses
`SecureRandom` and is **not** seeded — generated passwords are never
reproducible by design.

The public `random()` method returns the shared JDK `RandomGenerator`:

```java
import java.util.Map;
import java.util.random.RandomGenerator;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.WeightedPicker;

Fabricate fab = Fabricate.builder().seed(42L).build();
RandomGenerator random = fab.random();
WeightedPicker<String> status = WeightedPicker.of(random, Map.of("OPEN", 3, "PAID", 7));
String value = status.pick();
```

Calls through `random()` or a picker advance the same sequence as the fluent
API. Use a separate `Fabricate` per independent generation task.

## Modules and JPMS

Modular consumers need both `org.jspecify` (artifact `org.jspecify:jspecify`)
and `org.junit.jupiter.api` (artifact `org.junit.jupiter:junit-jupiter-api`)
on the **compile-time module path**, including their required dependencies,
because Fabricate declares them with `requires static transitive`. Maven marks
them optional, so consumers must add these dependencies explicitly. At runtime,
JSpecify may be absent; JUnit is needed when using the JUnit extension.

```java
module my.app {
    requires dev.havlli.fabricate;
    // Allow fab.of(...) / fab.fill(...) to construct records and POJOs reflectively.
    opens my.app.dto to dev.havlli.fabricate;
}
```

## License

Apache License 2.0 — see [LICENSE](LICENSE).

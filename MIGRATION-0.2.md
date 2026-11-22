# Migrating to fabricate 0.2.0

0.2.0 is the pre-1.0 transition to fluent generators and a smaller public API.
This guide covers increments 1–8 and the additive `Fake` deprecations in
increment 9.

Removed accessors and implementation types require source changes. `BeanFiller`,
`Address.builder()` / `Address.Builder`, the static bulk sink methods, and the
four `Fake` mutators remain available but are deprecated with
`since = "0.2.0", forRemoval = true`. Existing `Fake` data methods remain supported.

Examples marked `// Before (0.1.x)` illustrate the old API. All other Java
examples use the new API and are compiled by the documentation test. Imports
and local setup are included so each example can be read independently.

## 1. Use `Gen` and `fab.of(...)` for records and beans

The reflective implementation is now internal. `BeanFiller` is a deprecated
compatibility type; applications should obtain a `RecordGen<T>` through
`Fabricate.of(Class<T>)`. `fab.fill(type)` still works for a single value.

```java
// Before (0.1.x)
record Customer(String firstName, String email) {}
Fabricate fab = Fabricate.builder().seed(42L).build();
Customer customer = fab.fill(Customer.class); // backed by BeanFiller
List<Customer> customers = fab.repeat(10, () -> fab.fill(Customer.class));
List<String> emails = fab.repeat(10, fab.emails()::email);
```

```java
import java.util.List;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.Gen;
import dev.havlli.fabricate.RecordGen;

record Customer(String firstName, String email) {}
Fabricate fab = Fabricate.builder().seed(42L).build();
RecordGen<Customer> customerGen = fab.of(Customer.class).with("firstName", "Ada");
Customer customer = customerGen.get();
List<Customer> customers = customerGen.list(10);
Gen<String> emailGen = fab.gen(fab.internet()::email);
List<String> emails = emailGen.unique().list(10);
List<String> statuses = fab.oneOf("OPEN", "PAID").list(10);
```

`Gen<T>` supports `get`, `list`, `set`, sequential `stream`, `map`, `unique`,
`optional`, and `orNull`. `RecordGen.with` accepts a fixed value, supplier, or
`Gen`; it returns a new configuration. Records use their canonical constructor;
POJOs need an accessible no-arg constructor. Overrides are top-level only.
For a fixed null, use `.with("field", (Object) null)` to disambiguate overloads.

## 2. Replace person and address builders

```java
// Before (0.1.x)
Person person = fab.persons().person();
Person ada = fab.persons().builder().firstName("Ada").jobTitle("CEO").build();
Address address = fab.addresses().address();
Address fixed = Address.builder().street("1 Main St").city("Berlin")
        .state("Berlin").postalCode("10115").country("Germany").build();
```

```java
import java.util.List;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.PersonGen;
import dev.havlli.fabricate.model.Address;
import dev.havlli.fabricate.model.Person;

Fabricate fab = Fabricate.builder().seed(42L).build();
Person person = fab.person().get();
PersonGen executives = fab.person().firstName("Ada").jobTitle("CEO");
Person ada = executives.get();
Address address = fab.address().get();
Address fixed = new Address("1 Main St", "Berlin", "Berlin", "10115", "Germany");
List<Person> berliners = fab.person().address(fab.address().city("Berlin")).list(10);
List<Address> offices = fab.address().country("Germany").list(10);
```

`person()` and `address()` return immutable generator configurations. Capture
the result of each override; calling an override does not mutate the receiver.
Fields accept fixed values or generators. Person email and username follow the
generated names unless explicitly overridden. `uid` accepts a UUID, a UUID
generator, or a validated UUID string. An override skips the default draw for
that field, so later random draws can change. With no overrides, the dedicated
generators preserve the old providers' draw order.

The concrete `Persons.Builder` is no longer public. `Address.builder()` and
`Address.Builder` still work but are deprecated for removal. Use the canonical
`Address` constructor when all fields are fixed, or `fab.address()` to generate
unspecified fields.

## 3. Replace static bulk dumpers with sink instances

```java
// Before (0.1.x)
Sinks.toCsv(people, Path.of("people.csv"));
String csv = Sinks.toCsvString(people);
Sinks.toJsonLines(people, Path.of("people.jsonl"));
String jsonl = Sinks.toJsonLinesString(people);
Sinks.toSqlInserts(people, "person", Path.of("people.sql"));
String sql = Sinks.toSqlInsertsString(people, "person");
```

```java
import java.nio.file.Path;
import java.util.List;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.model.Person;
import dev.havlli.fabricate.sink.Sinks;

Fabricate fab = Fabricate.builder().seed(42L).build();
List<Person> people = fab.person().list(10);
Sinks.csv().write(people, Path.of("people.csv"));
String csv = Sinks.csv().writeToString(people);
Sinks.jsonLines().write(people, Path.of("people.jsonl"));
String jsonl = Sinks.jsonLines().writeToString(people);
Sinks.sql("person").write(people, Path.of("people.sql"));
String sql = Sinks.sql("person").writeToString(people);
Sinks.sql("person").batchSize(500).write(fab.person().stream().limit(1_000),
        Path.of("batch.sql"));
String json = Sinks.toJsonString(people.getFirst());
```

All six old bulk methods remain deprecated for removal. `Sinks.toJsonString`
continues to serialize one record and is **not deprecated**. Instance sinks
accept `Iterable` or `Stream` input and write to a `Path`, a caller-owned
`Writer`, or a string. SQL `batchSize(n)` returns a new sink configuration.

## 4. Use `@Fabricated` for JUnit parameter injection

```java
// Before (0.1.x)
@FabricateTest(seed = 42L)
class OrderTest {
    record Order(String product, int quantity) {}
    @Test
    void importsOrders(Fabricate fab) {
        Order order = fab.fill(Order.class);
        List<Person> people = fab.persons().list(10);
    }
}
```

```java
import java.util.List;
import org.junit.jupiter.api.Test;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.junit.FabricateTest;
import dev.havlli.fabricate.junit.Fabricated;
import dev.havlli.fabricate.junit.Seed;
import dev.havlli.fabricate.model.Person;

@FabricateTest(seed = 42L, locale = "de")
class OrderTest {
    record Order(String product, int quantity) {}
    @Test
    @Seed(7L)
    void importsOrders(@Fabricated Order order,
                       @Fabricated(size = 10) List<Person> people, Fabricate fab) {
        String email = fab.internet().email();
    }
}
```

`@Fabricated` uses the test's seed and locale, including method-level `@Seed`.
It supports records, fillable POJOs, `Person`, `Address`, and lists of concrete
supported classes. Lists require an explicit nonnegative `size`; zero is valid.
Raw lists and wildcard elements are rejected. Unannotated `Fabricate`, `Person`,
and `Address` injection remains supported. Leave `Fabricate` unannotated.

## 5. Replace all 14 plural accessors and renamed methods

These accessors have been removed from `Fabricate`, not merely deprecated:

| Removed accessor and old methods | Replacement |
|---|---|
| `names().firstName()` / `lastName()` / `fullName()` | `name().first()` / `last()` / `full()` |
| `emails().email()` / `email(first, last)` | `internet().email()` / `email(first, last)` |
| `phones().phoneNumber()` | `phone().number()` |
| `addresses().address()` | `address().get()` |
| `datesOfBirth().birthdate()` / `birthdate(min, max)` | `dateOfBirth().get()` / `get(min, max)` |
| `dates()` | `date()`; method names unchanged |
| `jobTitles().jobTitle()` | `job().title()` |
| `identities()` | `identity()`; `uuid()` and `username(...)` unchanged |
| `passwords().password(...)` | `password().get(...)`; all overloads |
| `numbers().intBetween(...)` / `longBetween(...)` / `doubleBetween(...)` | `number().between(...)`; int, long, and double overloads |
| `booleans().any()` | `bool().get()`; `withProbability(p)` unchanged |
| `texts()` | `text()`; method names unchanged |
| `files().fileName(...)` / `fileSizeBytes()` | `file().name(...)` / `sizeBytes()`; other names unchanged |
| `persons().person()` / `builder().firstName("Ada").build()` | `person().get()` / `person().firstName("Ada").get()` |

```java
// Before (0.1.x)
String first = fab.names().firstName();
String last = fab.names().lastName();
String full = fab.names().fullName();
String email = fab.emails().email(first, last);
String phone = fab.phones().phoneNumber();
Address address = fab.addresses().address();
LocalDate birth = fab.datesOfBirth().birthdate(18, 65);
LocalDate date = fab.dates().between(LocalDate.of(2020, 1, 1), LocalDate.of(2025, 1, 1));
String title = fab.jobTitles().jobTitle();
UUID id = fab.identities().uuid();
String password = fab.passwords().password(20);
int count = fab.numbers().intBetween(1, 10);
long bytes = fab.numbers().longBetween(1L, 100L);
double ratio = fab.numbers().doubleBetween(0.0, 1.0);
boolean active = fab.booleans().any();
String text = fab.texts().sentence();
String filename = fab.files().fileName();
long size = fab.files().fileSizeBytes();
Person person = fab.persons().person();
```

```java
import java.time.LocalDate;
import java.util.UUID;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.model.Address;
import dev.havlli.fabricate.model.Person;

Fabricate fab = Fabricate.builder().seed(42L).build();
String first = fab.name().first();
String last = fab.name().last();
String full = fab.name().full();
String email = fab.internet().email(first, last);
String phone = fab.phone().number();
Address address = fab.address().get();
LocalDate birth = fab.dateOfBirth().get(18, 65);
LocalDate date = fab.date().between(LocalDate.of(2020, 1, 1), LocalDate.of(2025, 1, 1));
String title = fab.job().title();
UUID id = fab.identity().uuid();
String password = fab.password().get(20);
int count = fab.number().between(1, 10);
long bytes = fab.number().between(1L, 100L);
double ratio = fab.number().between(0.0, 1.0);
boolean active = fab.bool().get();
String text = fab.text().sentence();
String filename = fab.file().name();
long size = fab.file().sizeBytes();
Person person = fab.person().get();
```

Integer and long bounds remain inclusive; the double upper bound is exclusive.
Use `L` or decimal literals to select the intended overload. `number().gaussian`
retains its name. Passwords still use `SecureRandom`, independently of the seed.

Names, phones, dates of birth, jobs, and identities retain `list(count)` and
`stream()`; names draw full names and identities draw UUIDs. Replace email
lists/streams with `fab.gen(fab.internet()::email).list(count)` / `.stream()`.
For old address component calls such as `addresses().city()`, read
`fab.address().get().city()`. Generating a full address consumes additional draws,
so this component migration does not preserve the old random sequence.

## 6. Replace `rng()` with `random(): RandomGenerator`

```java
// Before (0.1.x)
import dev.havlli.fabricate.random.Rng;
Rng random = fab.rng();
int index = random.nextInt(10);
```

```java
import java.util.random.RandomGenerator;
import dev.havlli.fabricate.Fabricate;

Fabricate fab = Fabricate.builder().seed(42L).build();
RandomGenerator random = fab.random();
int index = random.nextInt(10);
String role = fab.oneOf("admin", "user").get();
```

`random()` shares the fluent API's random state. Replace `Rng` convenience
methods such as `pick` with `fab.oneOf(...)`, `fab.choice(...)`, or JDK methods.
For an independent JDK generator, use `RandomGeneratorFactory`; there is no
public `Rng` constructor or factory to import in 0.2.0.

## 7. Replace `localeRegistry(...)` with `locale(LocaleData)`

```java
// Before (0.1.x)
import dev.havlli.fabricate.locale.LocaleRegistry;
Fabricate fab = Fabricate.builder().localeRegistry(LocaleRegistry.getDefault())
        .locale(Locale.GERMAN).seed(42L).build();
```

```java
import java.util.Locale;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.spi.LocaleData;

// Built-ins and registered services still use locale(Locale).
Fabricate german = Fabricate.builder().locale(Locale.GERMAN).seed(42L).build();
// Substitute your own LocaleData implementation to provide custom pools directly.
LocaleData data = german.locale();
Fabricate fab = Fabricate.builder().locale(data).seed(42L).build();
```

The last `locale(...)` call wins. `locale(LocaleData)` uses that object directly;
`locale(Locale)` selects registered data and clears any earlier direct override.
For discovery, implement the public `LocaleData` and `LocaleProvider` SPI and
register `META-INF/services/dev.havlli.fabricate.spi.LocaleProvider` on the
classpath, or `provides dev.havlli.fabricate.spi.LocaleProvider with ...` in a
module descriptor. Use `Fabricate.availableLocales()` to inspect registrations.

## 8. Stop importing implementation packages

`dev.havlli.fabricate.provider`, `dev.havlli.fabricate.locale`, and
`dev.havlli.fabricate.random` are no longer exported public packages. Concrete
provider constructors, locale implementations and their `INSTANCE` fields,
locale provider implementations, `LocaleRegistry`, and `Rng` have moved behind
non-exported implementation packages. Do not replace old imports with internal
ones: they are unsupported and inaccessible to modular consumers.

```java
// Before (0.1.x)
import dev.havlli.fabricate.provider.Names;
import dev.havlli.fabricate.locale.EnglishLocale;
import dev.havlli.fabricate.random.Rng;
Names names = new Names(EnglishLocale.INSTANCE, Rng.seeded(42L));
String name = names.fullName();
```

```java
import java.util.Locale;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.NameGen;
import dev.havlli.fabricate.spi.LocaleData;

Fabricate fab = Fabricate.builder().locale(Locale.ENGLISH).seed(42L).build();
NameGen names = fab.name();
String name = names.full();
LocaleData english = fab.locale();
```

Public generators live in `dev.havlli.fabricate` and must be obtained from
`Fabricate`, not implemented by applications. The `model`, `sink`, `junit`, and
`spi` packages remain public. Third parties may implement the locale SPI.

## 9. Update the five unchanged accessor names' return types

```java
// Before (0.1.x)
import dev.havlli.fabricate.provider.*;
Internet internet = fab.internet();
Geo geo = fab.geo();
Finance finance = fab.finance();
Commerce commerce = fab.commerce();
DevOps devops = fab.devops();
```

```java
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.InternetGen;
import dev.havlli.fabricate.GeoGen;
import dev.havlli.fabricate.FinanceGen;
import dev.havlli.fabricate.CommerceGen;
import dev.havlli.fabricate.DevOpsGen;

Fabricate fab = Fabricate.builder().seed(42L).build();
InternetGen internet = fab.internet();
GeoGen geo = fab.geo();
FinanceGen finance = fab.finance();
CommerceGen commerce = fab.commerce();
DevOpsGen devops = fab.devops();
String url = internet.url();
```

Their method names and seeded draw order are unchanged. Update explicit variable,
parameter, and return types; `var` callers generally need no edit here.
`InternetGen` also provides the email methods formerly reached through `emails()`.

## 10. Pass `RandomGenerator` to `WeightedPicker`

```java
// Before (0.1.x)
WeightedPicker<String> status = WeightedPicker.of(fab.rng(), Map.of("OPEN", 3, "PAID", 7));
WeightedPicker<String> role = WeightedPicker.<String>builder(fab.rng())
        .add("user", 9).add("admin", 1).build();
```

```java
import java.util.Map;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.WeightedPicker;

Fabricate fab = Fabricate.builder().seed(42L).build();
WeightedPicker<String> status = WeightedPicker.of(fab.random(), Map.of("OPEN", 3, "PAID", 7));
WeightedPicker<String> role = WeightedPicker.<String>builder(fab.random())
        .add("user", 9).add("admin", 1).build();
WeightedPicker<String> priority = fab.<String>weighted().add("normal", 9).add("urgent", 1).build();
```

Both factories accept any JDK `RandomGenerator`. Use `fab.random()` to share the
instance's sequence, or an independent generator when isolation is required.

## 11. Put static transitive dependencies on the compile-time module path

The module declares `requires static transitive org.jspecify` and
`requires static transitive org.junit.jupiter.api`. Modular consumers need both
`org.jspecify:jspecify` and `org.junit.jupiter:junit-jupiter-api`, plus their
required dependencies, on the **compile-time module path**, even if they only
use data generators. They are optional Maven dependencies, so add them explicitly.
JSpecify can be absent at runtime; using the JUnit extension requires JUnit.

```java
// Before (0.1.x)
module my.app {
    requires dev.havlli.fabricate;
    // Open DTOs for BeanFiller.
    opens my.app.dto to dev.havlli.fabricate;
}
```

```java
module my.app {
    requires dev.havlli.fabricate;
    // Allow fab.of(...) / fab.fill(...) to construct records and POJOs reflectively.
    opens my.app.dto to dev.havlli.fabricate;
}
```

The descriptor need not add direct `requires` clauses for those two transitive
dependencies. The compile-time module path must contain them. Reflection opens
are needed for DTO packages that are not otherwise accessible to Fabricate.

## 12. Treat `Fake` as a read-only scripts/demos facade

The default singleton starts with seed `0L`. Its shared sequence suits quick
scripts, but tests and production code should configure a local `Fabricate`.
All four global mutators still work and are deprecated for removal:
`seed(long)`, `unseed()`, `locale(Locale)`, and `use(Fabricate)`.

```java
// Before (0.1.x)
Fake.seed(42L);
Fake.locale(Locale.GERMAN);
String name = Fake.fullName();
Fake.unseed();
Fake.use(Fabricate.builder().seed(7L).build());
```

```java
import java.util.Locale;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.Fake;

Fabricate fab = Fabricate.builder().seed(42L).locale(Locale.GERMAN).build();
String name = fab.name().full();
Fabricate unseeded = Fabricate.builder().locale(Locale.GERMAN).build();
Fabricate another = Fabricate.builder().seed(7L).build();

// Zero-setup reads for scripts and demos:
String first = Fake.first();
String last = Fake.last();
String full = Fake.full();
String title = Fake.title();
int count = Fake.between(1, 10);
long bytes = Fake.between(1L, 100L);
double ratio = Fake.between(0.0, 1.0);
Fabricate defaults = Fake.fabricate(); // read the default instance only
```

`firstName`, `lastName`, `fullName`, `jobTitle`, `intBetween`, `longBetween`, and
`doubleBetween` remain supported alongside the shorter names. No existing data
method was removed. `Fake.phoneNumber()` remains the phone shortcut; `Fake`
does not add a generator-like `phone()` method.

From 1.0, public packages only accrete; an incompatible change requires a new
package. 0.2.0 is the pre-1.0 break. See the [README compatibility policy](README.md#compatibility-policy).

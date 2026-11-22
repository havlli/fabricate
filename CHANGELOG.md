# Changelog

## [Unreleased]

### Added

- Singular provider accessors: `name()`, `phone()`, `dateOfBirth()`, `date()`,
  `job()`, `identity()`, `password()`, `number()`, `bool()`, `text()`, and `file()`,
  with public provider-role interfaces in `dev.havlli.fabricate` and non-stuttering
  methods such as `name().full()`, `job().title()`, and `number().between(...)`.
  They share the existing providers and preserve seeded draw order.
- `Internet.email()` and `email(first, last)` share the existing locale-aware
  email provider; `internet()` retains its `Internet` return type.

- `@Fabricated` JUnit parameter injection for records, POJOs, people, addresses,
  and explicitly sized lists, using the test's configured seed and locale.
- Instance sinks via `Sinks.csv()`, `Sinks.jsonLines()`, and `Sinks.sql(table)`
  for iterable and stream input, with Path, Writer, and string output. SQL sinks
  support immutable `batchSize(n)` configuration.

- `Fabricate.person()` / `PersonGen` and `Fabricate.address()` / `AddressGen` for
  fluent generation with immutable fixed-value and generator overrides, nested
  address generators, and derived email and username defaults. Person uids accept
  UUID values, UUID generators, and validated UUID strings.
- `Fabricate.of(Class)` / `RecordGen<T>` for fluent record and bean generation
  with immutable fixed-value, supplier, and generator overrides.
- Public API snapshot test for contributors to review API changes explicitly.
- Fluent `Gen<T>` provider API via `Fabricate.gen(Supplier)` and
  `Fabricate.oneOf(T...)` / `oneOf(Collection)`, with immutable lists and ordered
  sets, sequential streams, independent uniqueness, mapping, and optional or
  nullable values using the owning instance's RNG.

### Deprecated

- `Fabricate.names()`, `emails()`, `phones()`, `addresses()`, `datesOfBirth()`,
  `dates()`, `jobTitles()`, `identities()`, `passwords()`, `numbers()`, `booleans()`,
  `texts()`, and `files()`, for removal since 0.2.0. Use the singular accessors,
  `internet().email(...)`, and `address().get()`. Legacy providers remain usable
  with the same draw order; passwords remain independent of the seed.

- `Sinks.toCsv`, `toCsvString`, `toJsonLines`, `toJsonLinesString`,
  `toSqlInserts`, and `toSqlInsertsString`, for removal since 0.2.0; use the
  instance sinks. `Sinks.toJsonString(Record)` remains supported.

- `Fabricate.persons()`, `Persons.builder()`, and `Persons.Builder`, for removal
  since 0.2.0; use `Fabricate.person()`.
- `Address.builder()` and `Address.Builder`, for removal since 0.2.0; use
  `Fabricate.address()` or the canonical `Address` constructor.
- `BeanFiller`, for removal; use `Fabricate.of(Class)` or `Fabricate.fill(Class)`.

### Fixed

- `Fake.locale(Locale)` now preserves the current seed, including unseeded mode.

## 0.1.0 - Unreleased

- Initial release of a lightweight Java 21 library for generating realistic
  random data, with locale support, reproducible generation, streaming,
  and an optional JUnit Jupiter extension.
- Rename Maven coordinates from `io.github.havlli:fabricate` to
  `dev.havlli:fabricate`, and packages and the JPMS module from
  `org.fabricate` to `dev.havlli.fabricate`. The library was never
  published under the old coordinates.

# Changelog

## [Unreleased]

## [0.2.0] - TBD

### Breaking changes

- Removed the public `dev.havlli.fabricate.provider`, `.locale`, and `.random`
  packages: concrete providers (including `Persons.Builder`), locale implementations
  and their `INSTANCE` fields, locale providers, `LocaleRegistry`, and `Rng` are
  now implementation details in non-exported packages. Direct construction/imports
  of these types are no longer supported.
- Replaced `Fabricate.rng()` with `random()`, returning `RandomGenerator`.
  `WeightedPicker.of(...)` and `builder(...)` now accept `RandomGenerator`.
- Replaced `Builder.localeRegistry(...)` with `locale(LocaleData)` for supplying
  third-party data directly. `locale(Locale)` still selects a registered locale.
- Removed plural accessors `names()`, `emails()`, `phones()`, `addresses()`,
  `datesOfBirth()`, `dates()`, `jobTitles()`, `identities()`, `passwords()`,
  `numbers()`, `booleans()`, `texts()`, `files()`, and `persons()`. Use the singular
  accessors, including `internet().email(...)`, `address()`, and `person()`.
- `internet()`, `geo()`, `finance()`, `commerce()`, and `devops()` now return
  `InternetGen`, `GeoGen`, `FinanceGen`, `CommerceGen`, and `DevOpsGen` respectively.
  Method names and seeded fluent draw order are unchanged.

### Added

- `Fake.first()`, `last()`, `full()`, `title()`, and `between(int, int)`,
  `between(long, long)`, `between(double, double)` shortcuts. Existing data
  methods remain available.
- [0.2.0 migration guide](MIGRATION-0.2.md), fluent API documentation, and
  compilation checks for documentation examples.

- Singular provider accessors: `name()`, `phone()`, `dateOfBirth()`, `date()`,
  `job()`, `identity()`, `password()`, `number()`, `bool()`, `text()`, and `file()`,
  with public provider-role interfaces in `dev.havlli.fabricate` and non-stuttering
  methods such as `name().full()`, `job().title()`, and `number().between(...)`.
  They share the existing providers and preserve seeded draw order.
- `InternetGen.email()` and `email(first, last)` share the existing locale-aware
  email provider.
- External module-path SPI integration coverage against the packaged jar, including
  direct `LocaleData` injection and checks that implementation packages are hidden.

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

### Changed

- Generator, bean, locale, random, provider, and sink implementations are excluded
  from exports, public API snapshots, and release Javadoc. Public utility types,
  model records, sinks, and the locale SPI retain their packages.

### Deprecated

- `Fake.seed(long)`, `unseed()`, `locale(Locale)`, and `use(Fabricate)`, for
  removal since 0.2.0. They still work for compatibility; treat `Fake` as a
  read-only scripts/demos facade and configure a local `Fabricate.builder()`
  for tests and production code.

- `Sinks.toCsv`, `toCsvString`, `toJsonLines`, `toJsonLinesString`,
  `toSqlInserts`, and `toSqlInsertsString`, for removal since 0.2.0; use the
  instance sinks. `Sinks.toJsonString(Record)` remains supported.

- `Address.builder()` and `Address.Builder`, for removal since 0.2.0; use
  `Fabricate.address()` or the canonical `Address` constructor.
- `BeanFiller`, for removal; use `Fabricate.of(Class)` or `Fabricate.fill(Class)`.

### Fixed

- `Fake.locale(Locale)` now preserves the current seed, including unseeded mode.

## 0.1.0 - 2026-10-12

- Initial release of a lightweight Java 21 library for generating realistic
  random data, with locale support, reproducible generation, streaming,
  and an optional JUnit Jupiter extension.
- Rename Maven coordinates from `io.github.havlli:fabricate` to
  `dev.havlli:fabricate`, and packages and the JPMS module from
  `org.fabricate` to `dev.havlli.fabricate`. The library was never
  published under the old coordinates.

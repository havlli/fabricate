# Changelog

## [Unreleased]

### Added

- `Fabricate.of(Class)` / `RecordGen<T>` for fluent record and bean generation
  with immutable fixed-value, supplier, and generator overrides.
- Public API snapshot test for contributors to review API changes explicitly.
- Fluent `Gen<T>` provider API via `Fabricate.gen(Supplier)` and
  `Fabricate.oneOf(T...)` / `oneOf(Collection)`, with immutable lists and ordered
  sets, sequential streams, independent uniqueness, mapping, and optional or
  nullable values using the owning instance's RNG.

### Deprecated

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

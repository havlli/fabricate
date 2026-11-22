package dev.havlli.fabricate;

import dev.havlli.fabricate.internal.LegacyProviders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import dev.havlli.fabricate.internal.locale.EnglishLocale;
import dev.havlli.fabricate.internal.provider.Emails;
import dev.havlli.fabricate.internal.provider.Internet;
import dev.havlli.fabricate.internal.random.Rng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SingularAccessorsTest {

    private static final LocalDate START = LocalDate.of(2020, 1, 1);
    private static final LocalDate END = LocalDate.of(2025, 12, 31);
    private static final UUID ID = UUID.fromString("01234567-89ab-4def-8123-456789abcdef");

    @ParameterizedTest(name = "{0}")
    @MethodSource("mappings")
    void singularAndLegacyCallsShareTheSameSequence(String name,
            Function<Fabricate, ?> singular, Function<Fabricate, ?> legacy) {
        for (Locale locale : List.of(Locale.ENGLISH, Locale.GERMAN, Locale.CHINESE)) {
            for (long seed : new long[] {0L, 42L, -1L}) {
                Fabricate actual = Fabricate.builder().locale(locale).seed(seed).build();
                Fabricate expected = Fabricate.builder().locale(locale).seed(seed).build();
                for (int i = 0; i < 5; i++) {
                    assertThat(singular.apply(actual)).as("%s, %s, seed %s", name, locale, seed)
                            .isEqualTo(legacy.apply(expected));
                    // Interleave legacy and singular draws on the same instance, then check
                    // another provider to detect extra draws or an independent RNG.
                    assertThat(legacy.apply(actual)).isEqualTo(singular.apply(expected));
                    assertThat(actual.identity().uuid()).isEqualTo(LegacyProviders.identities(expected).uuid());
                }
            }
        }
    }

    private static Stream<Arguments> mappings() {
        return Stream.of(
                mapping("first name", f -> f.name().first(), f -> LegacyProviders.names(f).firstName()),
                mapping("last name", f -> f.name().last(), f -> LegacyProviders.names(f).lastName()),
                mapping("full name", f -> f.name().full(), f -> LegacyProviders.names(f).fullName()),
                mapping("name list", f -> f.name().list(4), f -> LegacyProviders.names(f).list(4)),
                mapping("name stream", f -> f.name().stream().limit(4).toList(), f -> LegacyProviders.names(f).stream().limit(4).toList()),
                mapping("email", f -> f.internet().email(), f -> LegacyProviders.emails(f).email()),
                mapping("named email", f -> f.internet().email("Ada", "Lovelace"), f -> LegacyProviders.emails(f).email("Ada", "Lovelace")),
                mapping("phone", f -> f.phone().number(), f -> LegacyProviders.phones(f).phoneNumber()),
                mapping("phone list", f -> f.phone().list(4), f -> LegacyProviders.phones(f).list(4)),
                mapping("phone stream", f -> f.phone().stream().limit(4).toList(), f -> LegacyProviders.phones(f).stream().limit(4).toList()),
                mapping("address", f -> f.address().get(), f -> LegacyProviders.addresses(f).address()),
                mapping("address list", f -> f.address().list(4), f -> LegacyProviders.addresses(f).list(4)),
                mapping("address stream", f -> f.address().stream().limit(4).toList(), f -> LegacyProviders.addresses(f).stream().limit(4).toList()),
                mapping("date of birth", f -> f.dateOfBirth().get(), f -> LegacyProviders.datesOfBirth(f).birthdate()),
                mapping("ranged date of birth", f -> f.dateOfBirth().get(20, 30), f -> LegacyProviders.datesOfBirth(f).birthdate(20, 30)),
                mapping("date of birth list", f -> f.dateOfBirth().list(4), f -> LegacyProviders.datesOfBirth(f).list(4)),
                mapping("date of birth stream", f -> f.dateOfBirth().stream().limit(4).toList(), f -> LegacyProviders.datesOfBirth(f).stream().limit(4).toList()),
                mapping("job title", f -> f.job().title(), f -> LegacyProviders.jobTitles(f).jobTitle()),
                mapping("job list", f -> f.job().list(4), f -> LegacyProviders.jobTitles(f).list(4)),
                mapping("job stream", f -> f.job().stream().limit(4).toList(), f -> LegacyProviders.jobTitles(f).stream().limit(4).toList()),
                mapping("uuid", f -> f.identity().uuid(), f -> LegacyProviders.identities(f).uuid()),
                mapping("username", f -> f.identity().username("Ada", "Lovelace", ID), f -> LegacyProviders.identities(f).username("Ada", "Lovelace", ID)),
                mapping("identity list", f -> f.identity().list(4), f -> LegacyProviders.identities(f).list(4)),
                mapping("identity stream", f -> f.identity().stream().limit(4).toList(), f -> LegacyProviders.identities(f).stream().limit(4).toList()),
                mapping("int range", f -> f.number().between(1, 500), f -> LegacyProviders.numbers(f).intBetween(1, 500)),
                mapping("long range", f -> f.number().between(1L, 500L), f -> LegacyProviders.numbers(f).longBetween(1L, 500L)),
                mapping("double range", f -> f.number().between(1.0, 500.0), f -> LegacyProviders.numbers(f).doubleBetween(1.0, 500.0)),
                mapping("gaussian", f -> f.number().gaussian(100, 15), f -> LegacyProviders.numbers(f).gaussian(100, 15)),
                mapping("boolean", f -> f.bool().get(), f -> LegacyProviders.booleans(f).any()),
                mapping("probability", f -> f.bool().withProbability(0.7), f -> LegacyProviders.booleans(f).withProbability(0.7)),
                mapping("date range", f -> f.date().between(START, END), f -> LegacyProviders.dates(f).between(START, END)),
                mapping("recent date", f -> f.date().recent(30), f -> LegacyProviders.dates(f).recent(30)),
                mapping("future date", f -> f.date().future(30), f -> LegacyProviders.dates(f).future(30)),
                mapping("past date", f -> f.date().past(5), f -> LegacyProviders.dates(f).past(5)),
                mapping("date-time range", f -> f.date().localDateTimeBetween(START.atStartOfDay(), END.atStartOfDay()),
                        f -> LegacyProviders.dates(f).localDateTimeBetween(START.atStartOfDay(), END.atStartOfDay())),
                mapping("instant range", f -> f.date().instantBetween(Instant.EPOCH, Instant.ofEpochSecond(100000)),
                        f -> LegacyProviders.dates(f).instantBetween(Instant.EPOCH, Instant.ofEpochSecond(100000))),
                mapping("weekday", f -> f.date().weekday(), f -> LegacyProviders.dates(f).weekday()),
                mapping("month", f -> f.date().month(), f -> LegacyProviders.dates(f).month()),
                mapping("duration", f -> f.date().durationUpTo(3600), f -> LegacyProviders.dates(f).durationUpTo(3600)),
                mapping("working day", f -> f.date().workingDayBetween(START, END), f -> LegacyProviders.dates(f).workingDayBetween(START, END)),
                mapping("weekend day", f -> f.date().weekendDayBetween(START, END), f -> LegacyProviders.dates(f).weekendDayBetween(START, END)),
                mapping("upcoming working day", f -> f.date().workingDayInNext(30), f -> LegacyProviders.dates(f).workingDayInNext(30)),
                mapping("is weekend", f -> f.date().isWeekend(START), f -> LegacyProviders.dates(f).isWeekend(START)),
                mapping("word", f -> f.text().word(), f -> LegacyProviders.texts(f).word()),
                mapping("words", f -> f.text().words(4), f -> LegacyProviders.texts(f).words(4)),
                mapping("sentence", f -> f.text().sentence(), f -> LegacyProviders.texts(f).sentence()),
                mapping("paragraph", f -> f.text().paragraph(), f -> LegacyProviders.texts(f).paragraph()),
                mapping("paragraphs", f -> f.text().paragraphs(2), f -> LegacyProviders.texts(f).paragraphs(2)),
                mapping("slug", f -> f.text().slug(), f -> LegacyProviders.texts(f).slug()),
                mapping("hex", f -> f.text().hex(8), f -> LegacyProviders.texts(f).hex(8)),
                mapping("alphanumeric", f -> f.text().alphanumeric(8), f -> LegacyProviders.texts(f).alphanumeric(8)),
                mapping("approximate length", f -> f.text().aboutLength(40), f -> LegacyProviders.texts(f).aboutLength(40)),
                mapping("extension", f -> f.file().extension(), f -> LegacyProviders.files(f).extension()),
                mapping("file name", f -> f.file().name(), f -> LegacyProviders.files(f).fileName()),
                mapping("file name extension", f -> f.file().name(".pdf"), f -> LegacyProviders.files(f).fileName(".pdf")),
                mapping("file size", f -> f.file().sizeBytes(), f -> LegacyProviders.files(f).fileSizeBytes()),
                mapping("path", f -> f.file().path(), f -> LegacyProviders.files(f).path()),
                mapping("directory", f -> f.file().directory(), f -> LegacyProviders.files(f).directory()),
                mapping("mime type", f -> f.file().mimeType(), f -> LegacyProviders.files(f).mimeType()),
                mapping("mime type for extension", f -> f.file().mimeTypeFor(".pdf"), f -> LegacyProviders.files(f).mimeTypeFor(".pdf")));
    }

    private static Arguments mapping(String name, Function<Fabricate, ?> singular, Function<Fabricate, ?> legacy) {
        return Arguments.of(name, singular, legacy);
    }

    @Test
    void accessorsAndUndrawnStreamsConsumeNoRandomness() {
        Fabricate actual = Fabricate.builder().seed(42).build();
        Fabricate expected = Fabricate.builder().seed(42).build();
        actual.name().stream();
        actual.phone().stream();
        actual.dateOfBirth().stream();
        actual.job().stream();
        actual.identity().stream();
        actual.address().stream();
        actual.date();
        actual.password();
        actual.number();
        actual.bool();
        actual.text();
        actual.file();
        actual.internet();
        assertThat(actual.person().list(5)).isEqualTo(LegacyProviders.persons(expected).list(5));
        assertThat(actual.name().list(0)).isEmpty();
        assertThat(actual.phone().list(0)).isEmpty();
        assertThat(actual.dateOfBirth().list(0)).isEmpty();
        assertThat(actual.job().list(0)).isEmpty();
        assertThat(actual.identity().list(0)).isEmpty();
        assertThat(actual.identity().uuid()).isEqualTo(LegacyProviders.identities(expected).uuid());
    }

    @Test
    void implementationAccessorsAreRemovedAndSingularReturnsAreInterfaces() throws Exception {
        String[] removed = {"names", "emails", "phones", "addresses", "datesOfBirth", "dates",
                "jobTitles", "identities", "passwords", "numbers", "booleans", "texts", "files",
                "persons", "rng"};
        for (String accessor : removed) {
            assertThatThrownBy(() -> Fabricate.class.getMethod(accessor))
                    .isInstanceOf(NoSuchMethodException.class);
        }
        assertThat(Fabricate.class.getMethod("internet").getReturnType()).isEqualTo(InternetGen.class);
        assertThat(Fabricate.class.getMethod("geo").getReturnType()).isEqualTo(GeoGen.class);
        assertThat(Fabricate.class.getMethod("finance").getReturnType()).isEqualTo(FinanceGen.class);
        assertThat(Fabricate.class.getMethod("commerce").getReturnType()).isEqualTo(CommerceGen.class);
        assertThat(Fabricate.class.getMethod("devops").getReturnType()).isEqualTo(DevOpsGen.class);
    }

    @Test
    void passwordsPreserveTheSecureRandomContractWithoutConsumingTheSeededRng() {
        Fabricate actual = Fabricate.builder().seed(42).build();
        Fabricate expected = Fabricate.builder().seed(42).build();
        assertThat(actual.password().get()).hasSize(LegacyProviders.passwords(expected).password().length());
        assertThat(actual.password().get(23)).hasSize(LegacyProviders.passwords(expected).password(23).length());
        assertThat(actual.password().get(10, "x")).isEqualTo(LegacyProviders.passwords(expected).password(10, "x"));
        assertThat(actual.password().get(40, "abc")).matches("[abc]{40}");
        assertThatThrownBy(() -> actual.password().get(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> actual.password().get(1, "")).isInstanceOf(IllegalArgumentException.class);
        assertThat(actual.identity().uuid()).isEqualTo(LegacyProviders.identities(expected).uuid());
    }

    @Test
    void invalidArgumentsKeepLegacyValidation() {
        Fabricate fab = Fabricate.create();
        assertThatThrownBy(() -> fab.number().between(2, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> fab.number().between(2L, 1L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> fab.number().between(1.0, 1.0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> fab.bool().withProbability(Double.NaN)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> fab.dateOfBirth().get(30, 20)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> fab.name().list(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> fab.identity().username("Ada", "", ID)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void recentInstantUsesTheCurrentClockAndPreservesDrawCount() {
        Fabricate actual = Fabricate.builder().seed(42).build();
        Fabricate expected = Fabricate.builder().seed(42).build();
        Instant before = Instant.now().minusSeconds(60 * 30L);
        Instant instant = actual.date().recentInstant(30);
        LegacyProviders.dates(expected).recentInstant(30);
        assertThat(instant).isBetween(before.minusSeconds(1), Instant.now());
        assertThat(actual.identity().uuid()).isEqualTo(LegacyProviders.identities(expected).uuid());
    }


    @Test
    void standaloneInternetConstructorProvidesEnglishEmailsWithoutChangingNetworkDraws() {
        Internet actual = new Internet(Rng.seeded(42));
        Emails expected = new Emails(EnglishLocale.INSTANCE, Rng.seeded(42));
        assertThat(actual.email()).isEqualTo(expected.email());
        assertThat(actual.email("Ada", "Lovelace")).isEqualTo(expected.email("Ada", "Lovelace"));
        Fabricate fab = Fabricate.builder().seed(7).locale(Locale.GERMAN).build();
        Internet standalone = new Internet(Rng.seeded(7));
        assertThat(fab.internet().url()).isEqualTo(standalone.url());
        assertThat(fab.internet().ipv4()).isEqualTo(standalone.ipv4());
    }
}

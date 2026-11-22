package dev.havlli.fabricate;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import dev.havlli.fabricate.model.Address;
import dev.havlli.fabricate.model.Person;
import org.jspecify.annotations.Nullable;

/**
 * Zero-setup, default-seeded facade for scripts and demos.
 * Treat this facade as read-only: tests and production code should configure
 * their own local {@link Fabricate} via {@link Fabricate#builder()}.
 * Use {@link #fabricate()} only to read the default instance.
 *
 * {@snippet :
 * import dev.havlli.fabricate.Fake;
 * import dev.havlli.fabricate.model.Person;
 *
 * String name  = Fake.first();
 * String email = Fake.email();
 * Person p     = Fake.person();
 * int score    = Fake.between(0, 100);
 * }
 *
 * <h2>Determinism</h2>
 *
 * The default instance starts with seed {@code 0L}, keeping scripts and demos
 * reproducible without setup. Calls advance a shared random sequence, so
 * unrelated callers affect each other's results. Build a separate
 * {@code Fabricate} per test for isolation.
 *
 * <h2>Threading</h2>
 *
 * The default instance is a singleton and shares one RNG. Calls from
 * multiple threads produce non-deterministic interleaved sequences;
 * build per-thread {@code Fabricate} instances for parallel work.
 */
public final class Fake {

    /** Initial seed of the default instance. */
    public static final long DEFAULT_SEED = 0L;

    private static volatile Fabricate instance = Fabricate.builder().seed(DEFAULT_SEED).build();
    private static volatile @Nullable Long currentSeed = DEFAULT_SEED;

    private Fake() {}

    /**
     * Reseeds the underlying {@link Fabricate}.
     *
     * @deprecated Configure a local {@link Fabricate} via {@link Fabricate#builder()}.
     *             Use {@link #fabricate()} only to read the default instance.
     */
    @Deprecated(since = "0.2.0", forRemoval = true)
    public static void seed(long seed) {
        instance = Fabricate.builder().seed(seed).locale(instance.locale().locale()).build();
        currentSeed = seed;
    }

    /**
     * Switches to an unseeded instance.
     *
     * @deprecated Configure a local {@link Fabricate} via {@link Fabricate#builder()}.
     *             Use {@link #fabricate()} only to read the default instance.
     */
    @Deprecated(since = "0.2.0", forRemoval = true)
    public static void unseed() {
        instance = Fabricate.builder().locale(instance.locale().locale()).build();
        currentSeed = null;
    }

    /**
     * Switches the active locale, preserving the current seed mode.
     *
     * @deprecated Configure a local {@link Fabricate} via {@link Fabricate#builder()}.
     *             Use {@link #fabricate()} only to read the default instance.
     */
    @Deprecated(since = "0.2.0", forRemoval = true)
    public static void locale(Locale locale) {
        Fabricate.Builder builder = Fabricate.builder().locale(locale);
        Long seed = currentSeed;
        if (seed != null) {
            builder.seed(seed);
        }
        instance = builder.build();
    }

    /**
     * Replaces the underlying {@link Fabricate} entirely.
     *
     * @deprecated Configure a local {@link Fabricate} via {@link Fabricate#builder()}.
     *             Use {@link #fabricate()} only to read the default instance.
     */
    @Deprecated(since = "0.2.0", forRemoval = true)
    public static void use(Fabricate fabricate) {
        instance = fabricate;
    }

    /** Reads the default {@link Fabricate} instance; configure local instances with {@link Fabricate#builder()}. */
    public static Fabricate fabricate() {
        return instance;
    }

    // --- Names ---
    /** Draws a first name from the default instance. */
    public static String first()               { return instance.name().first(); }
    /** Draws a last name from the default instance. */
    public static String last()                { return instance.name().last(); }
    /** Draws a full name from the default instance. */
    public static String full()                { return instance.name().full(); }
    public static String firstName()           { return instance.name().first(); }
    public static String lastName()            { return instance.name().last(); }
    public static String fullName()            { return instance.name().full(); }

    // --- Contact ---
    public static String email()               { return instance.internet().email(); }
    public static String phoneNumber()         { return instance.phone().number(); }

    // --- Identity ---
    public static UUID uuid()                  { return instance.identity().uuid(); }

    // --- Address / Job ---
    public static Address address()            { return instance.address().get(); }
    public static String city()                { return instance.addresses.city(); }
    public static String country()             { return instance.addresses.country(); }
    /** Draws a job title from the default instance. */
    public static String title()               { return instance.job().title(); }
    public static String jobTitle()            { return instance.job().title(); }

    // --- Time ---
    public static LocalDate dateOfBirth()      { return instance.dateOfBirth().get(); }

    // --- Records ---
    public static Person person()              { return instance.person().get(); }
    public static <T> T fill(Class<T> type)    { return instance.fill(type); }

    // --- Primitives ---
    /** Uniform int in {@code [min, max]} (both inclusive). */
    public static int between(int min, int max)               { return instance.number().between(min, max); }
    /** Uniform long in {@code [min, max]} (both inclusive). */
    public static long between(long min, long max)            { return instance.number().between(min, max); }
    /** Uniform double in {@code [min, max)} (max exclusive). */
    public static double between(double min, double max)      { return instance.number().between(min, max); }
    public static int intBetween(int min, int max)            { return instance.number().between(min, max); }
    public static long longBetween(long min, long max)        { return instance.number().between(min, max); }
    public static double doubleBetween(double min, double max) { return instance.number().between(min, max); }
    public static boolean coinFlip()                          { return instance.bool().get(); }
    public static boolean withProbability(double p)           { return instance.bool().withProbability(p); }
    @SafeVarargs
    public static <T> T choice(T... values)                   { return instance.choice(values); }
    public static <T> T choice(List<T> values)                { return instance.choice(values); }
    public static <T> Optional<T> optional(double p, Supplier<T> s) { return instance.optional(p, s); }

    // --- Text ---
    public static String word()                { return instance.text().word(); }
    public static String sentence()            { return instance.text().sentence(); }
    public static String paragraph()           { return instance.text().paragraph(); }
    public static String slug()                { return instance.text().slug(); }
    public static String hex(int length)       { return instance.text().hex(length); }
    public static String alphanumeric(int length) { return instance.text().alphanumeric(length); }

    // --- Password ---
    public static String password()            { return instance.password().get(); }
    public static String password(int length)  { return instance.password().get(length); }

    // --- Internet ---
    public static String url()                 { return instance.internet().url(); }
    public static String urlWithQuery()        { return instance.internet().urlWithQuery(); }
    public static String domain()              { return instance.internet().domain(); }
    public static String ipv4()                { return instance.internet().ipv4(); }
    public static String ipv6()                { return instance.internet().ipv6(); }
    public static String ipv6Compressed()      { return instance.internet().ipv6Compressed(); }
    public static String macAddress()          { return instance.internet().macAddress(); }
    public static String userAgent()           { return instance.internet().userAgent(); }
    public static int port()                   { return instance.internet().port(); }
    public static String apiKey()              { return instance.internet().apiKey(); }
    public static String bearerToken()         { return instance.internet().bearerToken(); }
    public static String imageUrl()            { return instance.internet().imageUrl(); }
    public static String username()            { return instance.internet().username(); }

    // --- Geo ---
    public static double latitude()            { return instance.geo().latitude(); }
    public static double longitude()           { return instance.geo().longitude(); }
    public static String coordinate()          { return instance.geo().coordinate(); }
    public static String timezone()            { return instance.geo().timezone(); }
    public static String countryCodeIso2()     { return instance.geo().countryCodeIso2(); }
    public static String countryCodeIso3()     { return instance.geo().countryCodeIso3(); }
    public static String geohash(int length)   { return instance.geo().geohash(length); }

    // --- Files ---
    public static String fileName()            { return instance.file().name(); }
    public static String filePath()            { return instance.file().path(); }
    public static String mimeType()            { return instance.file().mimeType(); }
    public static long fileSizeBytes()         { return instance.file().sizeBytes(); }

    // --- Finance ---
    public static String creditCard()          { return instance.finance().creditCard(); }
    public static String creditCard(String brand) { return instance.finance().creditCard(brand); }
    public static String iban()                { return instance.finance().iban(); }
    public static String iban(String country)  { return instance.finance().iban(country); }
    public static String currencyCode()        { return instance.finance().currencyCode(); }
    public static String money()               { return instance.finance().money(); }

    // --- Commerce ---
    public static String productName()         { return instance.commerce().productName(); }
    public static String sku()                 { return instance.commerce().sku(); }
    public static String isbn13()              { return instance.commerce().isbn13(); }
    public static String colorName()           { return instance.commerce().colorName(); }
    public static String colorHex()            { return instance.commerce().colorHex(); }

    // --- DevOps ---
    public static String semver()              { return instance.devops().semver(); }
    public static String gitSha()              { return instance.devops().gitSha(); }
    public static String environment()         { return instance.devops().environment(); }
    public static String logLevel()            { return instance.devops().logLevel(); }
    public static int httpStatus()             { return instance.devops().httpStatus(); }
    public static String dockerImage()         { return instance.devops().dockerImage(); }

    // --- Dates ---
    public static LocalDate dateBetween(LocalDate from, LocalDate to) {
        return instance.date().between(from, to);
    }
    public static LocalDate dateRecent(int days)  { return instance.date().recent(days); }
    public static LocalDate dateFuture(int days)  { return instance.date().future(days); }
    public static LocalDate datePast(int years)   { return instance.date().past(years); }
    public static LocalDate workingDayInNext(int days) { return instance.date().workingDayInNext(days); }
    public static LocalDate workingDayBetween(LocalDate from, LocalDate to) {
        return instance.date().workingDayBetween(from, to);
    }
    public static LocalDate weekendDayBetween(LocalDate from, LocalDate to) {
        return instance.date().weekendDayBetween(from, to);
    }
}

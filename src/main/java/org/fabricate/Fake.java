package org.fabricate;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.fabricate.model.Address;
import org.fabricate.model.Person;

/**
 * Zero-setup static facade for one-shot fake-data needs.
 *
 * {@snippet :
 * String name  = Fake.firstName();
 * String email = Fake.email();
 * Person p     = Fake.person();
 * int score    = Fake.intBetween(0, 100);
 * }
 *
 * <h2>Determinism</h2>
 *
 * Out of the box the underlying {@link Fabricate} is seeded with
 * {@code 0L}, so {@code Fake.firstName()} returns the same value on every
 * JVM start. This keeps demos and quick scripts reproducible without any
 * setup; it also means two unrelated tests calling {@code Fake.email()}
 * will see correlated draws. For test isolation prefer building your own
 * {@link Fabricate} via {@link Fabricate#builder()}, or call
 * {@link #seed(long)} / {@link #unseed()} once at startup.
 *
 * <h2>Threading</h2>
 *
 * The default instance is a singleton and shares one RNG. Calls from
 * multiple threads will succeed but produce non-deterministic interleaved
 * sequences; for parallel work, build per-thread {@code Fabricate}
 * instances instead.
 */
public final class Fake {

    /** Seed used by the default instance until {@link #seed(long)} or {@link #unseed()} is called. */
    public static final long DEFAULT_SEED = 0L;

    private static volatile Fabricate instance = Fabricate.builder().seed(DEFAULT_SEED).build();

    private Fake() {}

    /** Reseeds the underlying {@link Fabricate}. */
    public static void seed(long seed) {
        instance = Fabricate.builder().seed(seed).locale(instance.locale().locale()).build();
    }

    /** Switches to a non-seeded (truly random) instance. */
    public static void unseed() {
        instance = Fabricate.builder().locale(instance.locale().locale()).build();
    }

    /** Switches the active locale, preserving the current seed mode. */
    public static void locale(Locale locale) {
        instance = Fabricate.builder().locale(locale).seed(DEFAULT_SEED).build();
    }

    /** Replaces the underlying {@link Fabricate} entirely. */
    public static void use(Fabricate fabricate) {
        instance = fabricate;
    }

    /** Returns the current underlying {@link Fabricate} for advanced use. */
    public static Fabricate fabricate() {
        return instance;
    }

    // --- Names ---
    public static String firstName()           { return instance.names().firstName(); }
    public static String lastName()            { return instance.names().lastName(); }
    public static String fullName()            { return instance.names().fullName(); }

    // --- Contact ---
    public static String email()               { return instance.emails().email(); }
    public static String phoneNumber()         { return instance.phones().phoneNumber(); }

    // --- Identity ---
    public static UUID uuid()                  { return instance.identities().uuid(); }

    // --- Address / Job ---
    public static Address address()            { return instance.addresses().address(); }
    public static String city()                { return instance.addresses().city(); }
    public static String country()             { return instance.addresses().country(); }
    public static String jobTitle()            { return instance.jobTitles().jobTitle(); }

    // --- Time ---
    public static LocalDate dateOfBirth()      { return instance.datesOfBirth().birthdate(); }

    // --- Records ---
    public static Person person()              { return instance.persons().person(); }
    public static <T> T fill(Class<T> type)    { return instance.fill(type); }

    // --- Primitives ---
    public static int intBetween(int min, int max)            { return instance.numbers().intBetween(min, max); }
    public static long longBetween(long min, long max)        { return instance.numbers().longBetween(min, max); }
    public static double doubleBetween(double min, double max) { return instance.numbers().doubleBetween(min, max); }
    public static boolean coinFlip()                          { return instance.booleans().any(); }
    public static boolean withProbability(double p)           { return instance.booleans().withProbability(p); }
    @SafeVarargs
    public static <T> T choice(T... values)                   { return instance.choice(values); }
    public static <T> T choice(List<T> values)                { return instance.choice(values); }
    public static <T> Optional<T> optional(double p, Supplier<T> s) { return instance.optional(p, s); }

    // --- Text ---
    public static String word()                { return instance.texts().word(); }
    public static String sentence()            { return instance.texts().sentence(); }
    public static String paragraph()           { return instance.texts().paragraph(); }
    public static String slug()                { return instance.texts().slug(); }
    public static String hex(int length)       { return instance.texts().hex(length); }
    public static String alphanumeric(int length) { return instance.texts().alphanumeric(length); }

    // --- Password ---
    public static String password()            { return instance.passwords().password(); }
    public static String password(int length)  { return instance.passwords().password(length); }

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
    public static String fileName()            { return instance.files().fileName(); }
    public static String filePath()            { return instance.files().path(); }
    public static String mimeType()            { return instance.files().mimeType(); }
    public static long fileSizeBytes()         { return instance.files().fileSizeBytes(); }

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
        return instance.dates().between(from, to);
    }
    public static LocalDate dateRecent(int days)  { return instance.dates().recent(days); }
    public static LocalDate dateFuture(int days)  { return instance.dates().future(days); }
    public static LocalDate datePast(int years)   { return instance.dates().past(years); }
}

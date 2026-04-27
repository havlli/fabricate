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
}

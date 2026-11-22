package dev.havlli.fabricate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import dev.havlli.fabricate.internal.BeanEngine;
import dev.havlli.fabricate.internal.DefaultAddressGen;
import dev.havlli.fabricate.internal.DefaultBoolGen;
import dev.havlli.fabricate.internal.DefaultDateGen;
import dev.havlli.fabricate.internal.DefaultDateOfBirthGen;
import dev.havlli.fabricate.internal.DefaultFileGen;
import dev.havlli.fabricate.internal.DefaultGen;
import dev.havlli.fabricate.internal.DefaultIdentityGen;
import dev.havlli.fabricate.internal.DefaultJobGen;
import dev.havlli.fabricate.internal.DefaultNameGen;
import dev.havlli.fabricate.internal.DefaultNumberGen;
import dev.havlli.fabricate.internal.DefaultPasswordGen;
import dev.havlli.fabricate.internal.DefaultPersonGen;
import dev.havlli.fabricate.internal.DefaultPhoneGen;
import dev.havlli.fabricate.internal.DefaultRecordGen;
import dev.havlli.fabricate.internal.DefaultTextGen;
import dev.havlli.fabricate.internal.locale.LocaleRegistry;
import dev.havlli.fabricate.internal.provider.Addresses;
import dev.havlli.fabricate.internal.provider.Booleans;
import dev.havlli.fabricate.internal.provider.Commerce;
import dev.havlli.fabricate.internal.provider.Dates;
import dev.havlli.fabricate.internal.provider.DatesOfBirth;
import dev.havlli.fabricate.internal.provider.DevOps;
import dev.havlli.fabricate.internal.provider.Emails;
import dev.havlli.fabricate.internal.provider.Files;
import dev.havlli.fabricate.internal.provider.Finance;
import dev.havlli.fabricate.internal.provider.Geo;
import dev.havlli.fabricate.internal.provider.Identities;
import dev.havlli.fabricate.internal.provider.Internet;
import dev.havlli.fabricate.internal.provider.JobTitles;
import dev.havlli.fabricate.internal.provider.Names;
import dev.havlli.fabricate.internal.provider.Numbers;
import dev.havlli.fabricate.internal.provider.Passwords;
import dev.havlli.fabricate.internal.provider.Phones;
import dev.havlli.fabricate.internal.provider.Texts;
import dev.havlli.fabricate.internal.random.Rng;
import dev.havlli.fabricate.spi.LocaleData;
import org.jspecify.annotations.Nullable;

/**
 * Main entry point for the library.
 *
 * Use {@link #create()} for sensible defaults (English locale, random seed)
 * or {@link #builder()} for full control:
 *
 * {@snippet :
 * Fabricate fab = Fabricate.builder()
 *     .locale(Locale.CHINESE)
 *     .seed(42L)
 *     .build();
 *
 * String name = fab.name().full();
 * String email = fab.internet().email();
 * int score = fab.number().between(1, 500);
 * String title = fab.job().title();
 * Person p = fab.person().get();
 * Person ceo = fab.person().jobTitle("CEO").get(); // override one field
 * }
 *
 * Instances are immutable and thread-confined: create one per thread, or
 * one per test, rather than sharing across threads.
 */
public final class Fabricate {

    private final LocaleData locale;
    private final Rng rng;
    // Seeded providers share the same RNG; public access is via generator interfaces.
    private final Names names;
    private final Emails emails;
    private final Phones phones;
    // Shared with the package-private delegates in Fake and Templates.
    final Addresses addresses;
    private final DatesOfBirth datesOfBirth;
    private final JobTitles jobTitles;
    private final Identities identities;
    private final Passwords passwords;
    private final Numbers numbers;
    private final Booleans booleans;
    private final Texts texts;
    private final Internet internet;
    private final Files files;
    private final Finance finance;
    private final Commerce commerce;
    private final DevOps devops;
    private final Dates dates;
    private final Geo geo;
    private final NameGen nameGen;
    private final PhoneGen phoneGen;
    private final DateOfBirthGen dateOfBirthGen;
    private final DateGen dateGen;
    private final JobGen jobGen;
    private final IdentityGen identityGen;
    private final PasswordGen passwordGen;
    private final NumberGen numberGen;
    private final BoolGen boolGen;
    private final TextGen textGen;
    private final FileGen fileGen;
    private final BeanEngine beanEngine;
    private final Templates templates;

    private Fabricate(LocaleData locale, Rng rng) {
        this.locale = locale;
        this.rng = rng;
        this.names = new Names(locale, rng);
        this.emails = new Emails(locale, rng);
        this.phones = new Phones(locale, rng);
        this.addresses = new Addresses(locale, rng);
        this.datesOfBirth = new DatesOfBirth(rng);
        this.jobTitles = new JobTitles(locale, rng);
        this.identities = new Identities(rng);
        this.passwords = new Passwords();
        this.numbers = new Numbers(rng);
        this.booleans = new Booleans(rng);
        this.texts = new Texts(rng);
        this.internet = new Internet(locale, rng);
        this.files = new Files(rng);
        this.finance = new Finance(rng);
        this.commerce = new Commerce(rng);
        this.devops = new DevOps(rng);
        this.dates = new Dates(rng);
        this.geo = new Geo(rng);
        this.nameGen = new DefaultNameGen(names);
        this.phoneGen = new DefaultPhoneGen(phones);
        this.dateOfBirthGen = new DefaultDateOfBirthGen(datesOfBirth);
        this.dateGen = new DefaultDateGen(dates);
        this.jobGen = new DefaultJobGen(jobTitles);
        this.identityGen = new DefaultIdentityGen(identities);
        this.passwordGen = new DefaultPasswordGen(passwords);
        this.numberGen = new DefaultNumberGen(numbers);
        this.boolGen = new DefaultBoolGen(booleans);
        this.textGen = new DefaultTextGen(texts);
        this.fileGen = new DefaultFileGen(files);
        this.beanEngine = new BeanEngine(this, addresses);
        this.templates = new Templates(this);
    }

    public static Fabricate create() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Locales available in the default locale registry: built-ins plus
     * any {@link dev.havlli.fabricate.spi.LocaleProvider} discovered on the module path.
     */
    public static Set<Locale> availableLocales() {
        return LocaleRegistry.getDefault().available();
    }

    public LocaleData locale() { return locale; }

    /** Returns the random generator shared by this instance’s fluent API. */
    public RandomGenerator random() { return rng.delegate(); }

    /** Returns the name generator sharing this instance's provider. */
    public NameGen name() { return nameGen; }

    /** Returns the phone generator sharing this instance's provider. */
    public PhoneGen phone() { return phoneGen; }

    /** Returns the date-of-birth generator sharing this instance's provider. */
    public DateOfBirthGen dateOfBirth() { return dateOfBirthGen; }

    /** Returns the date generator sharing this instance's provider. */
    public DateGen date() { return dateGen; }
    public GeoGen geo() { return geo; }

    /** Returns the job generator sharing this instance's provider. */
    public JobGen job() { return jobGen; }

    /** Returns the identity generator sharing this instance's provider. */
    public IdentityGen identity() { return identityGen; }

    /** Returns the password generator sharing this instance's provider. */
    public PasswordGen password() { return passwordGen; }

    /** Returns the number generator sharing this instance's provider. */
    public NumberGen number() { return numberGen; }

    /** Returns the boolean generator sharing this instance's provider. */
    public BoolGen bool() { return boolGen; }

    /** Returns the text generator sharing this instance's provider. */
    public TextGen text() { return textGen; }
    public InternetGen internet() { return internet; }

    /** Returns the file generator sharing this instance's provider. */
    public FileGen file() { return fileGen; }
    public FinanceGen finance() { return finance; }
    public CommerceGen commerce() { return commerce; }
    public DevOpsGen devops() { return devops; }

    /**
     * Returns a new fluent person generator bound to this instance's RNG.
     * With no overrides, draws match the legacy person provider's draw order.
     * Overridden fields skip default generation and consume no RNG themselves;
     * supplied generators may consume random values on each draw.
     *
     * @return a new immutable person generator configuration
     */
    public PersonGen person() {
        return new DefaultPersonGen(this);
    }

    /**
     * Returns a new fluent address generator bound to this instance's RNG.
     * With no overrides, draws preserve the original address provider’s draw order.
     * Overridden fields skip default generation and consume no RNG themselves;
     * supplied generators may consume random values on each draw.
     *
     * @return a new immutable address generator configuration
     */
    public AddressGen address() {
        return new DefaultAddressGen(this, addresses);
    }

    /** Reflectively populates a record or POJO type with random values. */
    public <T> T fill(Class<T> type) {
        return beanEngine.fill(type);
    }

    /**
     * Returns a fluent generator for a record or POJO with a no-arg constructor,
     * using the same heuristics as {@link #fill(Class)}. Creating the generator
     * consumes no random values. Overrides can be added with {@link RecordGen#with(String, Object)}.
     *
     * @param <T> the generated record or POJO type
     * @param type the type to populate
     * @return a new generator bound to this instance's RNG
     * @throws IllegalArgumentException if the type is neither a record nor a fillable POJO
     * @throws NullPointerException if {@code type} is null
     */
    public <T> RecordGen<T> of(Class<T> type) {
        BeanEngine.validateType(type);
        return new DefaultRecordGen<>(beanEngine, type, random());
    }

    /** Returns the {@link Templates} expander bound to this Fabricate's providers. */
    public Templates templates() {
        return templates;
    }

    /** Convenience: {@code templates().expand(template)}. */
    public String expand(String template) {
        return templates.expand(template);
    }

    /**
     * Wraps {@code source} in a fluent generator bound to this Fabricate's RNG.
     * The source is invoked only when values are requested.
     *
     * @param <T> the generated value type, which may be nullable
     * @param source the supplier of generated values
     * @return a new fluent generator
     * @throws NullPointerException if {@code source} is null
     */
    public <T extends @Nullable Object> Gen<T> gen(Supplier<? extends T> source) {
        return new DefaultGen<>(Objects.requireNonNull(source, "source"), random());
    }

    /**
     * Returns a generator that uniformly picks from a snapshot of {@code values}.
     * Each draw uses the same RNG sequence as {@link #choice(Object...)}.
     *
     * @param <T> the generated value type, which may be nullable
     * @param values the choices, allowing duplicates and nulls
     * @return a new fluent generator
     * @throws NullPointerException if {@code values} is null
     * @throws IllegalArgumentException if {@code values} is empty
     */
    @SafeVarargs
    public final <T extends @Nullable Object> Gen<T> oneOf(T... values) {
        Objects.requireNonNull(values, "values");
        if (values.length == 0) {
            throw new IllegalArgumentException("oneOf requires at least one value");
        }
        List<T> snapshot = new ArrayList<>(values.length);
        for (T value : values) {
            snapshot.add(value);
        }
        return gen(() -> snapshot.get(numbers.intBetween(0, snapshot.size() - 1)));
    }

    /**
     * Returns a generator that uniformly picks from a snapshot of {@code values}
     * in iteration order. Each draw uses the same RNG sequence as
     * {@link #choice(List)} for a list in that order.
     *
     * @param <T> the generated value type, which may be nullable
     * @param values the choices, allowing duplicates and nulls
     * @return a new fluent generator
     * @throws NullPointerException if {@code values} is null
     * @throws IllegalArgumentException if {@code values} is empty
     */
    public <T extends @Nullable Object> Gen<T> oneOf(Collection<? extends T> values) {
        Objects.requireNonNull(values, "values");
        List<T> snapshot = new ArrayList<>(values);
        if (snapshot.isEmpty()) {
            throw new IllegalArgumentException("oneOf requires at least one value");
        }
        return gen(() -> snapshot.get(rng.nextInt(snapshot.size())));
    }

    /** Picks one of the literal values uniformly. */
    @SafeVarargs
    public final <T> T choice(T... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("choice requires at least one value");
        }
        return values[numbers.intBetween(0, values.length - 1)];
    }

    /** Picks one of the values uniformly. */
    public <T> T choice(List<T> values) {
        return rng.pick(values);
    }

    /** A new list containing {@code count} elements drawn without replacement from {@code values}. */
    public <T> List<T> sample(List<T> values, int count) {
        if (count < 0) throw new IllegalArgumentException("count must be >= 0");
        if (count > values.size()) {
            throw new IllegalArgumentException("count " + count + " exceeds pool size " + values.size());
        }
        java.util.ArrayList<T> pool = new java.util.ArrayList<>(values);
        fisherYates(pool);
        return List.copyOf(pool.subList(0, count));
    }

    /** A new list with the given values in randomised order. */
    public <T> List<T> shuffle(List<T> values) {
        java.util.ArrayList<T> copy = new java.util.ArrayList<>(values);
        fisherYates(copy);
        return List.copyOf(copy);
    }

    private <T> void fisherYates(java.util.List<T> list) {
        for (int i = list.size() - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            T tmp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, tmp);
        }
    }

    /** Calls {@code supplier} {@code count} times and returns the results in order. */
    public <T> List<T> repeat(int count, Supplier<T> supplier) {
        if (count < 0) throw new IllegalArgumentException("count must be >= 0");
        java.util.ArrayList<T> out = new java.util.ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            out.add(supplier.get());
        }
        return List.copyOf(out);
    }

    /**
     * Returns the supplier's value with probability {@code presentProbability},
     * otherwise {@link Optional#empty()}. Useful for testing nullable fields.
     */
    public <T> Optional<T> optional(double presentProbability, Supplier<T> supplier) {
        return booleans.withProbability(presentProbability)
                ? Optional.of(supplier.get())
                : Optional.empty();
    }

    /** Convenience entry point for {@link WeightedPicker#builder(RandomGenerator)} bound to this RNG. */
    public <T> WeightedPicker.Builder<T> weighted() {
        return WeightedPicker.builder(random());
    }

    public static final class Builder {

        private Locale locale = Locale.ENGLISH;
        private @Nullable Long seed;
        private @Nullable LocaleData localeData;

        private Builder() {}

        /** Selects a registered locale, replacing any previously supplied locale data. */
        public Builder locale(Locale locale) {
            this.locale = Objects.requireNonNull(locale, "locale");
            this.localeData = null;
            return this;
        }

        public Builder seed(long seed) {
            this.seed = seed;
            return this;
        }

        /** Uses the supplied locale data directly, replacing any previous locale selection. */
        public Builder locale(LocaleData locale) {
            this.localeData = Objects.requireNonNull(locale, "locale");
            return this;
        }

        public Fabricate build() {
            LocaleData data = localeData != null ? localeData : LocaleRegistry.getDefault().get(locale);
            Rng rng = (seed != null) ? Rng.seeded(seed) : Rng.create();
            return new Fabricate(data, rng);
        }
    }
}

package org.fabricate;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.fabricate.locale.LocaleRegistry;
import org.fabricate.provider.Addresses;
import org.fabricate.provider.Booleans;
import org.fabricate.provider.Commerce;
import org.fabricate.provider.Dates;
import org.fabricate.provider.DatesOfBirth;
import org.fabricate.provider.DevOps;
import org.fabricate.provider.Emails;
import org.fabricate.provider.Files;
import org.fabricate.provider.Finance;
import org.fabricate.provider.Identities;
import org.fabricate.provider.Internet;
import org.fabricate.provider.JobTitles;
import org.fabricate.provider.Names;
import org.fabricate.provider.Numbers;
import org.fabricate.provider.Passwords;
import org.fabricate.provider.Persons;
import org.fabricate.provider.Phones;
import org.fabricate.provider.Texts;
import org.fabricate.random.Rng;
import org.fabricate.spi.LocaleData;
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
 * Person p = fab.persons().person();
 * Person ceo = fab.persons().builder().jobTitle("CEO").build(); // override one field
 * }
 *
 * Instances are immutable and thread-confined: create one per thread, or
 * one per test, rather than sharing across threads.
 */
public final class Fabricate {

    private final LocaleData locale;
    private final Rng rng;
    private final Names names;
    private final Emails emails;
    private final Phones phones;
    private final Addresses addresses;
    private final DatesOfBirth datesOfBirth;
    private final JobTitles jobTitles;
    private final Identities identities;
    private final Passwords passwords;
    private final Persons persons;
    private final Numbers numbers;
    private final Booleans booleans;
    private final Texts texts;
    private final Internet internet;
    private final Files files;
    private final Finance finance;
    private final Commerce commerce;
    private final DevOps devops;
    private final Dates dates;
    private final BeanFiller beanFiller;

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
        this.persons = new Persons(names, emails, phones, addresses, datesOfBirth, jobTitles, identities);
        this.numbers = new Numbers(rng);
        this.booleans = new Booleans(rng);
        this.texts = new Texts(rng);
        this.internet = new Internet(rng);
        this.files = new Files(rng);
        this.finance = new Finance(rng);
        this.commerce = new Commerce(rng);
        this.devops = new DevOps(rng);
        this.dates = new Dates(rng);
        this.beanFiller = new BeanFiller(this);
    }

    public static Fabricate create() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Locales available in the default {@link LocaleRegistry}: built-ins plus
     * any {@link org.fabricate.spi.LocaleProvider} discovered on the module path.
     */
    public static Set<Locale> availableLocales() {
        return LocaleRegistry.getDefault().available();
    }

    public LocaleData locale()       { return locale; }
    public Rng rng()                 { return rng; }
    public Names names()             { return names; }
    public Emails emails()           { return emails; }
    public Phones phones()           { return phones; }
    public Addresses addresses()     { return addresses; }
    public DatesOfBirth datesOfBirth()  { return datesOfBirth; }
    public Dates dates()             { return dates; }
    public JobTitles jobTitles()     { return jobTitles; }
    public Identities identities()   { return identities; }
    public Passwords passwords()     { return passwords; }
    public Persons persons()         { return persons; }
    public Numbers numbers()         { return numbers; }
    public Booleans booleans()       { return booleans; }
    public Texts texts()             { return texts; }
    public Internet internet()       { return internet; }
    public Files files()             { return files; }
    public Finance finance()         { return finance; }
    public Commerce commerce()       { return commerce; }
    public DevOps devops()           { return devops; }

    /** Reflectively populates an arbitrary record type with random values. */
    public <T> T fill(Class<T> type) {
        return beanFiller.fill(type);
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

    /** Convenience entry point for {@link WeightedPicker#builder(Rng)} bound to this RNG. */
    public <T> WeightedPicker.Builder<T> weighted() {
        return WeightedPicker.builder(rng);
    }

    public static final class Builder {

        private Locale locale = Locale.ENGLISH;
        private @Nullable Long seed;
        private LocaleRegistry registry = LocaleRegistry.getDefault();

        private Builder() {}

        public Builder locale(Locale locale) {
            this.locale = Objects.requireNonNull(locale, "locale");
            return this;
        }

        public Builder seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Builder localeRegistry(LocaleRegistry registry) {
            this.registry = Objects.requireNonNull(registry, "registry");
            return this;
        }

        public Fabricate build() {
            LocaleData data = registry.get(locale);
            Rng rng = (seed != null) ? Rng.seeded(seed) : Rng.create();
            return new Fabricate(data, rng);
        }
    }
}

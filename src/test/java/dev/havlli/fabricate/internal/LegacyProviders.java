package dev.havlli.fabricate.internal;

import dev.havlli.fabricate.Fabricate;

import dev.havlli.fabricate.internal.provider.*;
import dev.havlli.fabricate.internal.random.Rng;

/** Test-only access to the original algorithms, sharing a reference instance's RNG. */
public final class LegacyProviders {
    private LegacyProviders() {}

    public static Names names(Fabricate fab) {
        return new Names(fab.locale(), new Rng(fab.random()));
    }

    public static Emails emails(Fabricate fab) {
        return new Emails(fab.locale(), new Rng(fab.random()));
    }

    public static Phones phones(Fabricate fab) {
        return new Phones(fab.locale(), new Rng(fab.random()));
    }

    public static Addresses addresses(Fabricate fab) {
        return new Addresses(fab.locale(), new Rng(fab.random()));
    }

    public static DatesOfBirth datesOfBirth(Fabricate fab) {
        return new DatesOfBirth(new Rng(fab.random()));
    }

    public static JobTitles jobTitles(Fabricate fab) {
        return new JobTitles(fab.locale(), new Rng(fab.random()));
    }

    public static Identities identities(Fabricate fab) {
        return new Identities(new Rng(fab.random()));
    }

    public static Passwords passwords(Fabricate fab) {
        return new Passwords();
    }

    public static Numbers numbers(Fabricate fab) {
        return new Numbers(new Rng(fab.random()));
    }

    public static Booleans booleans(Fabricate fab) {
        return new Booleans(new Rng(fab.random()));
    }

    public static Texts texts(Fabricate fab) {
        return new Texts(new Rng(fab.random()));
    }

    public static Files files(Fabricate fab) {
        return new Files(new Rng(fab.random()));
    }

    public static Dates dates(Fabricate fab) {
        return new Dates(new Rng(fab.random()));
    }

    public static Persons persons(Fabricate fab) {
        return new Persons(names(fab), emails(fab), phones(fab), addresses(fab), datesOfBirth(fab), jobTitles(fab), identities(fab));
    }

}

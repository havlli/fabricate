package org.fabricate;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Tiny property-testing helper: runs a body across N deterministic seeds and,
 * on the first failure, reports the seed that reproduces it.
 *
 * {@snippet :
 * Trials.run(100, fab -> {
 *     Person p = fab.persons().person();
 *     assertThat(p.email()).contains("@");
 * });
 *
 * // failure message includes the failing seed; reproduce with:
 * Fabricate fab = Fabricate.builder().seed(42L).build();
 * }
 *
 * <p>This intentionally trades sophistication (shrinking, generators, type
 * inference) for zero dependencies. For full property-based testing reach for
 * jqwik, JUnit-Quickcheck, or similar; this helper just covers the 90 % case
 * of "run my fake-data assertion many times and tell me which seed broke".
 */
public final class Trials {

    private Trials() {}

    /** Runs {@code body} for seeds {@code 0 .. trials-1}, propagating failures with the failing seed. */
    public static void run(int trials, Consumer<Fabricate> body) {
        run(trials, Locale.ENGLISH, body);
    }

    /** Runs {@code body} for seeds {@code 0 .. trials-1} with a fixed locale. */
    public static void run(int trials, Locale locale, Consumer<Fabricate> body) {
        if (trials < 0) throw new IllegalArgumentException("trials must be >= 0");
        for (long seed = 0; seed < trials; seed++) {
            Fabricate fab = Fabricate.builder().locale(locale).seed(seed).build();
            try {
                body.accept(fab);
            } catch (Throwable t) {
                throw new AssertionError(
                        "Trial failed at seed=" + seed
                                + ". Reproduce with Fabricate.builder().locale("
                                + locale + ").seed(" + seed + "L).build()",
                        t);
            }
        }
    }

    /** Replays a single seed — handy in a debugger after a {@link #run} failure. */
    public static void replay(long seed, Consumer<Fabricate> body) {
        replay(seed, Locale.ENGLISH, body);
    }

    /** Replays a single seed with a fixed locale. */
    public static void replay(long seed, Locale locale, Consumer<Fabricate> body) {
        body.accept(Fabricate.builder().locale(locale).seed(seed).build());
    }
}

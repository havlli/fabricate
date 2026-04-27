package org.fabricate;

/**
 * Thrown by {@link Unique#get()} when the retry budget is consumed without
 * finding a fresh value — typically because the underlying source has a
 * finite pool that has been fully drawn.
 */
public final class UniqueExhaustedException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    private final int seenCount;
    private final int retryBudget;

    public UniqueExhaustedException(int seenCount, int retryBudget) {
        super("Unique source exhausted after " + retryBudget
                + " attempts (already produced " + seenCount + " distinct values)");
        this.seenCount = seenCount;
        this.retryBudget = retryBudget;
    }

    public int seenCount() {
        return seenCount;
    }

    public int retryBudget() {
        return retryBudget;
    }
}

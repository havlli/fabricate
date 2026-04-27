package org.fabricate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongFunction;

/**
 * Monotonic counters and cyclic pickers — the deterministic counterpart to random pickers.
 *
 * Use a counter to generate stable, increasing IDs (no collisions, even across seeds), and
 * a cycle to walk a fixed list in order.
 *
 * {@snippet :
 * Sequence.Counter id    = Sequence.counter(1000);
 * long next              = id.next();                          // 1000, 1001, 1002, ...
 * Sequence.Counter named = Sequence.counter("USR-", 1);
 * String username        = named.format();                     // "USR-1", "USR-2", ...
 * Sequence.Cycle<String> roles = Sequence.cycle("admin", "user", "guest");
 * String r               = roles.next();                       // "admin", "user", "guest", "admin", ...
 * }
 */
public final class Sequence {

    private Sequence() {}

    public static Counter counter() { return new Counter("", 0L); }

    public static Counter counter(long start) { return new Counter("", start); }

    public static Counter counter(String prefix, long start) {
        return new Counter(Objects.requireNonNull(prefix, "prefix"), start);
    }

    @SafeVarargs
    public static <T> Cycle<T> cycle(T... values) {
        if (values.length == 0) throw new IllegalArgumentException("cycle requires at least one value");
        return new Cycle<>(List.of(values));
    }

    public static <T> Cycle<T> cycle(List<T> values) {
        if (values.isEmpty()) throw new IllegalArgumentException("cycle requires at least one value");
        return new Cycle<>(List.copyOf(values));
    }

    public static final class Counter {

        private final String prefix;
        private final AtomicLong counter;

        private Counter(String prefix, long start) {
            this.prefix = prefix;
            this.counter = new AtomicLong(start);
        }

        /** Next monotonic value. Thread-safe. */
        public long next() {
            return counter.getAndIncrement();
        }

        /** Next value rendered as {@code prefix + value}. */
        public String format() {
            return prefix + counter.getAndIncrement();
        }

        /** Next value rendered via the supplied formatter. */
        public String format(LongFunction<String> formatter) {
            return formatter.apply(counter.getAndIncrement());
        }
    }

    public static final class Cycle<T> {

        private final List<T> values;
        private final AtomicLong index = new AtomicLong();

        private Cycle(List<T> values) {
            this.values = values;
        }

        /** The next value in the cycle, wrapping when the end is reached. Thread-safe. */
        public T next() {
            long i = index.getAndIncrement();
            return values.get((int) (Math.floorMod(i, values.size())));
        }
    }
}

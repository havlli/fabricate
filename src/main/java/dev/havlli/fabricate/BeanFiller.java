package dev.havlli.fabricate;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import dev.havlli.fabricate.model.Address;
import dev.havlli.fabricate.model.Person;

/**
 * Fills records and JavaBean-style POJOs with sensible random values inferred
 * from each component or property's declared type and name.
 *
 * <p>Records are constructed via their canonical constructor. POJOs require a
 * public or accessible no-arg constructor; values are pushed via setters first
 * (any {@code setFoo(X)} method) and then via writable non-static fields for
 * properties without setters.
 *
 * <p>Component- and property-name heuristics are matched case-insensitively
 * after stripping non-letter characters, so {@code first_name},
 * {@code FIRST_NAME}, and {@code firstName} all match. Recognised names include
 * common identity, contact, address, internet, finance, commerce, devops, and
 * geo terms.
 *
 * <p>Supported types: all primitives and their boxes, {@link String},
 * {@link UUID}, {@link LocalDate}, {@link LocalDateTime}, {@link Instant},
 * {@link BigDecimal}, {@link URI}, {@link Address}, {@link Person}, any nested
 * record (filled recursively), and any nested POJO satisfying the constructor
 * rule above. Unsupported types raise {@link IllegalArgumentException} with the
 * offending field and type included.
 *
 * @deprecated Use {@link Fabricate#of(Class)} for a fluent generator or
 *             {@link Fabricate#fill(Class)} for a single value.
 */
@Deprecated(since = "0.2.0", forRemoval = true)
public final class BeanFiller {

    private final BeanEngine engine;

    BeanFiller(Fabricate fab) {
        this.engine = new BeanEngine(fab);
    }

    /** Fills a record or POJO type by invoking its constructor with generated values. */
    public <T> T fill(Class<T> type) {
        return engine.fill(type);
    }
}

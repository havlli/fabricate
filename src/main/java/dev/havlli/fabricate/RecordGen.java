package dev.havlli.fabricate;

import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

/**
 * A fluent source of records or POJOs, populated by type and name heuristics.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} (or by composing those instances) and must not implement
 * this interface. Methods may be added in future versions.
 *
 * <p>Each {@code with} call returns a new immutable configuration, leaving the
 * receiver unchanged. Repeating a name replaces its earlier override. Names
 * match exactly against record components or POJO properties (setter-derived
 * names or writable fields). Overrides apply only to the top-level object;
 * nested records and POJOs are still filled by heuristics.
 *
 * <p>Overridden components skip heuristic generation entirely, so overrides
 * change the RNG sequence of later components. Suppliers and generators are
 * invoked on each draw and may themselves consume random values. Derived
 * generators share the RNG and override sources and are not thread-safe.
 * Fixed values are retained by reference, without copying.
 *
 * @param <T> the generated record or POJO type
 */
public interface RecordGen<T> extends Gen<T> {

    /**
     * Returns a new generator with a fixed value for the named component or property.
     * Assignability is checked immediately, treating primitive types as their boxes.
     * Null is allowed only for non-primitive targets; use {@code (Object) null}
     * to select this overload when passing a null literal.
     *
     * @param name the exact component or property name
     * @param value the fixed value, possibly null
     * @return a new generator with this override
     * @throws IllegalArgumentException if the name is unknown or the value is incompatible
     * @throws NullPointerException if {@code name} is null
     */
    RecordGen<T> with(String name, @Nullable Object value);

    /**
     * Returns a new generator that draws the named component or property from a supplier.
     * The name is checked immediately. Each supplied result is checked for
     * boxing-aware assignability on generation, allowing null only for non-primitive
     * targets; an incompatible result throws {@link IllegalArgumentException}
     * naming the component or property.
     *
     * @param name the exact component or property name
     * @param supplier the source to invoke on each draw
     * @return a new generator with this override
     * @throws IllegalArgumentException if the name is unknown
     * @throws NullPointerException if {@code name} or {@code supplier} is null
     */
    RecordGen<T> with(String name, Supplier<?> supplier);

    /**
     * Returns a new generator that draws the named component or property from a generator.
     * The name is checked immediately. Each generated result is checked for
     * boxing-aware assignability on generation, allowing null only for non-primitive
     * targets; an incompatible result throws {@link IllegalArgumentException}
     * naming the component or property.
     *
     * @param name the exact component or property name
     * @param gen the generator to invoke on each draw
     * @return a new generator with this override
     * @throws IllegalArgumentException if the name is unknown
     * @throws NullPointerException if {@code name} or {@code gen} is null
     */
    RecordGen<T> with(String name, Gen<?> gen);
}

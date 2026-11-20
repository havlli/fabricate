package dev.havlli.fabricate.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test parameter for generation by {@link FabricateExtension} using the
 * per-test {@link dev.havlli.fabricate.Fabricate} instance and its seed and locale.
 * {@link dev.havlli.fabricate.model.Person} and
 * {@link dev.havlli.fabricate.model.Address} use their dedicated generators;
 * other records and fillable POJOs use
 * {@link dev.havlli.fabricate.Fabricate#of(Class)}, which reflectively fills
 * components and properties using the usual name and type heuristics.
 *
 * <p>A {@code List<T>} requires {@link #size()} to be set to a nonnegative value
 * and {@code T} to be a concrete supported class. Raw lists, wildcards, and
 * other element types are unsupported. To inject the {@code Fabricate} instance
 * itself, leave the parameter unannotated.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Fabricated {

    /**
     * Number of elements to generate for a {@code List} parameter; required for
     * lists and ignored for single values. Zero produces an empty list.
     *
     * @return the list size, or {@code -1} when unset
     */
    int size() default -1;
}

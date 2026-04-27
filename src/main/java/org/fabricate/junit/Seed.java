package org.fabricate.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overrides the seed configured on {@link FabricateTest} for a single test method.
 *
 * Useful when one test in a class needs reproducibility independent of the
 * class-level seed, e.g. to pin a regression scenario.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Seed {
    long value();
}

package org.fabricate.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Marks a test class as participating in the {@link FabricateExtension}.
 *
 * Test methods may declare any of the following parameters and have them
 * resolved automatically:
 * <ul>
 *   <li>{@link org.fabricate.Fabricate} — a fresh instance per test.</li>
 *   <li>{@link org.fabricate.model.Person} — a fully-randomized person.</li>
 *   <li>{@link org.fabricate.model.Address} — a fully-randomized address.</li>
 * </ul>
 *
 * The class-level {@link #seed()} is the default; individual methods can
 * override via {@link Seed @Seed}. {@link #locale()} accepts a BCP-47 tag
 * such as {@code "en"} or {@code "zh"}.
 *
 * {@snippet :
 * @FabricateTest(seed = 42L)
 * class UserServiceTest {
 *     @Test
 *     void importsUsers(Fabricate fab) {
 *         List<Person> users = fab.persons().list(100);
 *         // ...
 *     }
 * }
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(FabricateExtension.class)
public @interface FabricateTest {

    /** Seed for the {@link org.fabricate.Fabricate} instance. {@code Long.MIN_VALUE} means "no seed (random)". */
    long seed() default Long.MIN_VALUE;

    /** BCP-47 language tag; empty means English. */
    String locale() default "";
}

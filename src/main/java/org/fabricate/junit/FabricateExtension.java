package org.fabricate.junit;

import java.util.Locale;
import java.util.Optional;
import org.fabricate.Fabricate;
import org.fabricate.model.Address;
import org.fabricate.model.Person;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * JUnit 5 extension that provisions a fresh {@link Fabricate} per test and
 * resolves it (along with {@link Person} and {@link Address}) as a method
 * parameter.
 *
 * Activated transitively by {@link FabricateTest @FabricateTest}; see that
 * annotation for usage.
 */
public final class FabricateExtension implements BeforeEachCallback, ParameterResolver {

    private static final Namespace NAMESPACE = Namespace.create(FabricateExtension.class);
    private static final String FABRICATE_KEY = "fabricate";

    @Override
    public void beforeEach(ExtensionContext context) {
        Fabricate fab = buildFabricate(context);
        context.getStore(NAMESPACE).put(FABRICATE_KEY, fab);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();
        return type == Fabricate.class || type == Person.class || type == Address.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Store store = extensionContext.getStore(NAMESPACE);
        Fabricate fab = store.get(FABRICATE_KEY, Fabricate.class);
        Class<?> type = parameterContext.getParameter().getType();
        if (type == Fabricate.class) {
            return fab;
        }
        if (type == Person.class) {
            return fab.persons().person();
        }
        if (type == Address.class) {
            return fab.addresses().address();
        }
        throw new IllegalStateException("Unsupported parameter type: " + type);
    }

    private static Fabricate buildFabricate(ExtensionContext context) {
        FabricateTest classCfg = context.getRequiredTestClass().getAnnotation(FabricateTest.class);
        Long methodSeed = context.getTestMethod()
                .flatMap(m -> Optional.ofNullable(m.getAnnotation(Seed.class)))
                .map(Seed::value)
                .orElse(null);

        Fabricate.Builder builder = Fabricate.builder();
        if (classCfg != null && !classCfg.locale().isEmpty()) {
            builder.locale(Locale.forLanguageTag(classCfg.locale()));
        }
        if (methodSeed != null) {
            builder.seed(methodSeed);
        } else if (classCfg != null && classCfg.seed() != Long.MIN_VALUE) {
            builder.seed(classCfg.seed());
        }
        return builder.build();
    }
}

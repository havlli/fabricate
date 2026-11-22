package dev.havlli.fabricate.junit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import dev.havlli.fabricate.Fabricate;
import dev.havlli.fabricate.Gen;
import dev.havlli.fabricate.model.Address;
import dev.havlli.fabricate.model.Person;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * JUnit 5 extension that provisions a fresh {@link Fabricate} per test and
 * resolves it (along with {@link Person} and {@link Address}) as a method
 * parameter. Parameters marked with {@link Fabricated} also support records,
 * fillable POJOs, and explicitly sized lists of supported values.
 *
 * Activated transitively by {@link FabricateTest @FabricateTest}; see that
 * annotation for usage.
 */
public final class FabricateExtension implements BeforeEachCallback, ParameterResolver {

    private static final Namespace NAMESPACE = Namespace.create(FabricateExtension.class);
    private static final String FABRICATE_KEY = "fabricate";

    public FabricateExtension() {}

    @Override
    public void beforeEach(ExtensionContext context) {
        Fabricate fab = buildFabricate(context);
        context.getStore(NAMESPACE).put(FABRICATE_KEY, fab);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();
        return parameterContext.isAnnotated(Fabricated.class)
                || type == Fabricate.class || type == Person.class || type == Address.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Store store = extensionContext.getStore(NAMESPACE);
        Fabricate fab = store.get(FABRICATE_KEY, Fabricate.class);
        Optional<Fabricated> fabricated = parameterContext.findAnnotation(Fabricated.class);
        if (fabricated.isPresent()) {
            return resolveFabricated(fab, parameterContext.getParameter().getParameterizedType(),
                    fabricated.get().size());
        }
        Class<?> type = parameterContext.getParameter().getType();
        if (type == Fabricate.class) {
            return fab;
        }
        if (type == Person.class) {
            return fab.person().get();
        }
        if (type == Address.class) {
            return fab.address().get();
        }
        throw new IllegalStateException("Unsupported parameter type: " + type);
    }

    private static Object resolveFabricated(Fabricate fab, Type type, int size) {
        try {
            if (type instanceof ParameterizedType parameterized && parameterized.getRawType() == List.class) {
                if (size < 0) {
                    throw new IllegalArgumentException("size is required for lists and must be >= 0");
                }
                Type elementType = parameterized.getActualTypeArguments()[0];
                if (elementType instanceof Class<?> elementClass) {
                    return generator(fab, elementClass).list(size);
                }
                throw new IllegalArgumentException("List element type must be a concrete supported class");
            }
            if (type == List.class) {
                throw new IllegalArgumentException("Raw List is unsupported; use List<T> with a concrete supported class"
                        + "; size is required for lists and must be >= 0");
            }
            Type valueType = type instanceof ParameterizedType parameterized ? parameterized.getRawType() : type;
            if (valueType instanceof Class<?> valueClass) {
                return generator(fab, valueClass).get();
            }
            throw new IllegalArgumentException("Expected a supported class or List<T>");
        } catch (RuntimeException e) {
            throw new ParameterResolutionException(
                    "Cannot resolve @Fabricated parameter type " + type.getTypeName() + ": " + e.getMessage(), e);
        }
    }

    private static Gen<?> generator(Fabricate fab, Class<?> type) {
        if (type == Fabricate.class) {
            throw new IllegalArgumentException("Fabricate must be injected without @Fabricated");
        }
        if (type == Person.class) {
            return fab.person();
        }
        if (type == Address.class) {
            return fab.address();
        }
        return fab.of(type);
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

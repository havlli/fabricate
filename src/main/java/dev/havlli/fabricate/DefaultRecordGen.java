package dev.havlli.fabricate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import dev.havlli.fabricate.random.Rng;
import org.jspecify.annotations.Nullable;

final class DefaultRecordGen<T> extends DefaultGen<T> implements RecordGen<T> {

    private final BeanEngine engine;
    private final Class<T> type;
    private final Rng rng;
    private final Map<String, List<Class<?>>> properties;
    private final Map<String, Supplier<?>> overrides;

    DefaultRecordGen(BeanEngine engine, Class<T> type, Rng rng) {
        this(engine, type, rng, BeanEngine.properties(type), Map.of());
    }

    private DefaultRecordGen(BeanEngine engine, Class<T> type, Rng rng,
                             Map<String, List<Class<?>>> properties,
                             Map<String, Supplier<?>> overrides) {
        super(() -> engine.fill(type, overrides), rng);
        this.engine = engine;
        this.type = type;
        this.rng = rng;
        this.properties = properties;
        this.overrides = overrides;
    }

    @Override
    public RecordGen<T> with(String name, @Nullable Object value) {
        for (Class<?> target : targets(name)) {
            BeanEngine.validateValue(name, target, value);
        }
        return overriding(name, () -> value);
    }

    @Override
    public RecordGen<T> with(String name, Supplier<?> supplier) {
        targets(name);
        return overriding(name, Objects.requireNonNull(supplier, "supplier"));
    }

    @Override
    public RecordGen<T> with(String name, Gen<?> gen) {
        targets(name);
        return overriding(name, Objects.requireNonNull(gen, "gen"));
    }

    private List<Class<?>> targets(String name) {
        Objects.requireNonNull(name, "name");
        @Nullable List<Class<?>> targets = properties.get(name);
        if (targets == null) {
            throw new IllegalArgumentException("Unknown component/property '" + name
                    + "' on " + type.getName() + "; valid names: " + properties.keySet());
        }
        return targets;
    }

    private RecordGen<T> overriding(String name, Supplier<?> source) {
        Map<String, Supplier<?>> updated = new HashMap<>(overrides);
        updated.put(name, source);
        return new DefaultRecordGen<>(engine, type, rng, properties, Map.copyOf(updated));
    }
}

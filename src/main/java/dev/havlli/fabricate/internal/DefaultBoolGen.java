package dev.havlli.fabricate.internal;

import dev.havlli.fabricate.BoolGen;
import dev.havlli.fabricate.internal.provider.Booleans;

public final class DefaultBoolGen implements BoolGen {

    private final Booleans provider;

    public DefaultBoolGen(Booleans provider) {
        this.provider = provider;
    }

    @Override
    public boolean get() {
        return provider.any();
    }

    @Override
    public boolean withProbability(double p) {
        return provider.withProbability(p);
    }
}

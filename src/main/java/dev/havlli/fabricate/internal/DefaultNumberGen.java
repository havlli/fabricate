package dev.havlli.fabricate.internal;

import dev.havlli.fabricate.NumberGen;
import dev.havlli.fabricate.internal.provider.Numbers;

public final class DefaultNumberGen implements NumberGen {

    private final Numbers provider;

    public DefaultNumberGen(Numbers provider) {
        this.provider = provider;
    }

    @Override
    public int between(int min, int max) {
        return provider.intBetween(min, max);
    }

    @Override
    public long between(long min, long max) {
        return provider.longBetween(min, max);
    }

    @Override
    public double between(double min, double max) {
        return provider.doubleBetween(min, max);
    }

    @Override
    public double gaussian(double mean, double stddev) {
        return provider.gaussian(mean, stddev);
    }
}

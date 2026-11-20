package dev.havlli.fabricate;
import dev.havlli.fabricate.provider.Numbers;

final class DefaultNumberGen implements NumberGen {

    private final Numbers provider;

    DefaultNumberGen(Fabricate fab) {
        this.provider = fab.numbers;
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

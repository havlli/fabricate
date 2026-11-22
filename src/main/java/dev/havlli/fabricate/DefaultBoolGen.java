package dev.havlli.fabricate;
import dev.havlli.fabricate.provider.Booleans;

final class DefaultBoolGen implements BoolGen {

    private final Booleans provider;

    DefaultBoolGen(Fabricate fab) {
        this.provider = fab.booleans;
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

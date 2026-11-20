package dev.havlli.fabricate;

import java.util.List;
import java.util.stream.Stream;
import dev.havlli.fabricate.provider.JobTitles;

final class DefaultJobGen implements JobGen {

    private final JobTitles provider;

    DefaultJobGen(Fabricate fab) {
        this.provider = fab.jobTitles;
    }

    @Override
    public String title() {
        return provider.jobTitle();
    }

    @Override
    public Stream<String> stream() {
        return provider.stream();
    }

    @Override
    public List<String> list(int count) {
        return provider.list(count);
    }
}

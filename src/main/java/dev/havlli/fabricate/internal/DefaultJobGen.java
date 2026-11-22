package dev.havlli.fabricate.internal;

import java.util.List;
import java.util.stream.Stream;
import dev.havlli.fabricate.JobGen;
import dev.havlli.fabricate.internal.provider.JobTitles;

public final class DefaultJobGen implements JobGen {

    private final JobTitles provider;

    public DefaultJobGen(JobTitles provider) {
        this.provider = provider;
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

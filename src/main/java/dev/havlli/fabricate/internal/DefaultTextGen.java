package dev.havlli.fabricate.internal;

import dev.havlli.fabricate.TextGen;
import dev.havlli.fabricate.internal.provider.Texts;

public final class DefaultTextGen implements TextGen {

    private final Texts provider;

    public DefaultTextGen(Texts provider) {
        this.provider = provider;
    }

    @Override
    public String word() {
        return provider.word();
    }

    @Override
    public String words(int count) {
        return provider.words(count);
    }

    @Override
    public String sentence() {
        return provider.sentence();
    }

    @Override
    public String paragraph() {
        return provider.paragraph();
    }

    @Override
    public String paragraphs(int count) {
        return provider.paragraphs(count);
    }

    @Override
    public String slug() {
        return provider.slug();
    }

    @Override
    public String alphanumeric(int length) {
        return provider.alphanumeric(length);
    }

    @Override
    public String hex(int length) {
        return provider.hex(length);
    }

    @Override
    public String aboutLength(int averageLength) {
        return provider.aboutLength(averageLength);
    }
}

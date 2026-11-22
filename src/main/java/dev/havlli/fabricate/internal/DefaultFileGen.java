package dev.havlli.fabricate.internal;

import dev.havlli.fabricate.FileGen;
import dev.havlli.fabricate.internal.provider.Files;

public final class DefaultFileGen implements FileGen {

    private final Files provider;

    public DefaultFileGen(Files provider) {
        this.provider = provider;
    }

    @Override
    public String extension() {
        return provider.extension();
    }

    @Override
    public String name() {
        return provider.fileName();
    }

    @Override
    public String name(String extension) {
        return provider.fileName(extension);
    }

    @Override
    public String path() {
        return provider.path();
    }

    @Override
    public String directory() {
        return provider.directory();
    }

    @Override
    public String mimeType() {
        return provider.mimeType();
    }

    @Override
    public String mimeTypeFor(String extension) {
        return provider.mimeTypeFor(extension);
    }

    @Override
    public long sizeBytes() {
        return provider.fileSizeBytes();
    }
}

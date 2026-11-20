package dev.havlli.fabricate;
import dev.havlli.fabricate.provider.Files;

final class DefaultFileGen implements FileGen {

    private final Files provider;

    DefaultFileGen(Fabricate fab) {
        this.provider = fab.files;
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

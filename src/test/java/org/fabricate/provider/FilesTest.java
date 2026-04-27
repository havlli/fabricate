package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class FilesTest {

    @Test
    void extension_isLowercaseToken() {
        Files files = new Files(Rng.seeded(0L));

        for (int i = 0; i < 50; i++) {
            assertThat(files.extension()).matches("[a-z0-9]+");
        }
    }

    @Test
    void fileName_hasSlugAndExtension() {
        Files files = new Files(Rng.seeded(1L));

        for (int i = 0; i < 50; i++) {
            String name = files.fileName();
            assertThat(name).matches("[a-z]+(-[a-z]+){0,3}\\.[a-z0-9]+");
        }
    }

    @Test
    void fileName_acceptsExplicitExtensionWithOrWithoutDot() {
        Files files = new Files(Rng.seeded(2L));

        assertThat(files.fileName("pdf")).endsWith(".pdf");
        assertThat(files.fileName(".pdf")).endsWith(".pdf");
    }

    @Test
    void path_startsWithRootAndHasFileLeaf() {
        Files files = new Files(Rng.seeded(3L));

        for (int i = 0; i < 30; i++) {
            String p = files.path();
            assertThat(p).matches("/(var|etc|usr|opt|home|tmp|srv)(/[a-z]+){2,4}/[a-z]+(-[a-z]+){0,3}\\.[a-z0-9]+");
        }
    }

    @Test
    void directory_startsWithRootAndHasNoFileLeaf() {
        Files files = new Files(Rng.seeded(4L));

        for (int i = 0; i < 30; i++) {
            String d = files.directory();
            assertThat(d).matches("/(var|etc|usr|opt|home|tmp|srv)(/[a-z]+){2,5}");
        }
    }

    @Test
    void mimeType_isRecognisableMime() {
        Files files = new Files(Rng.seeded(5L));

        for (int i = 0; i < 30; i++) {
            String mime = files.mimeType();
            assertThat(mime).matches("[a-z]+/[A-Za-z0-9.+\\-]+");
        }
    }

    @Test
    void mimeTypeFor_resolvesKnownExtensions() {
        Files files = new Files(Rng.seeded(6L));

        assertThat(files.mimeTypeFor("json")).isEqualTo("application/json");
        assertThat(files.mimeTypeFor(".json")).isEqualTo("application/json");
        assertThat(files.mimeTypeFor("PNG")).isEqualTo("image/png");
    }

    @Test
    void mimeTypeFor_unknownReturnsOctetStream() {
        Files files = new Files(Rng.seeded(7L));

        assertThat(files.mimeTypeFor("unobtainium")).isEqualTo("application/octet-stream");
    }

    @Test
    void fileSizeBytes_isWithinReasonableRange() {
        Files files = new Files(Rng.seeded(8L));

        long min = 1L << 10;
        long max = 1L << 24;
        for (int i = 0; i < 200; i++) {
            long size = files.fileSizeBytes();
            assertThat(size).isBetween(min, max);
        }
    }
}

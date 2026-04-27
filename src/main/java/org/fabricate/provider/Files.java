package org.fabricate.provider;

import java.util.List;
import java.util.Map;
import org.fabricate.random.Rng;

/**
 * File-system primitives: file names, paths, extensions, MIME types.
 *
 * {@snippet :
 * String name = fab.files().fileName();         // ut-iure-debitis.pdf
 * String pdf  = fab.files().fileName("pdf");    // <slug>.pdf
 * String mime = fab.files().mimeType();         // application/json
 * String full = fab.files().path();             // /var/log/foo/bar/quux.log
 * }
 */
public final class Files {

    private static final List<String> EXTENSIONS = List.of(
            "txt", "md", "rst", "log", "csv", "tsv", "json", "yaml", "yml", "toml", "xml",
            "html", "css", "js", "ts", "java", "py", "rb", "go", "rs", "kt", "swift",
            "sql", "sh", "bat", "ps1",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "odt",
            "png", "jpg", "jpeg", "gif", "svg", "webp", "bmp",
            "mp3", "wav", "flac", "ogg",
            "mp4", "mov", "avi", "mkv", "webm",
            "zip", "tar", "gz", "tgz", "7z", "bz2");

    private static final Map<String, String> MIME_BY_EXT = Map.ofEntries(
            Map.entry("txt", "text/plain"),
            Map.entry("md", "text/markdown"),
            Map.entry("html", "text/html"),
            Map.entry("css", "text/css"),
            Map.entry("js", "application/javascript"),
            Map.entry("json", "application/json"),
            Map.entry("xml", "application/xml"),
            Map.entry("yaml", "application/yaml"),
            Map.entry("yml", "application/yaml"),
            Map.entry("csv", "text/csv"),
            Map.entry("pdf", "application/pdf"),
            Map.entry("png", "image/png"),
            Map.entry("jpg", "image/jpeg"),
            Map.entry("jpeg", "image/jpeg"),
            Map.entry("gif", "image/gif"),
            Map.entry("svg", "image/svg+xml"),
            Map.entry("webp", "image/webp"),
            Map.entry("mp3", "audio/mpeg"),
            Map.entry("wav", "audio/wav"),
            Map.entry("mp4", "video/mp4"),
            Map.entry("webm", "video/webm"),
            Map.entry("zip", "application/zip"),
            Map.entry("gz", "application/gzip"),
            Map.entry("tar", "application/x-tar"));

    private static final List<String> ROOT_DIRS = List.of(
            "/var", "/etc", "/usr", "/opt", "/home", "/tmp", "/srv");

    private final Rng rng;
    private final Numbers numbers;
    private final Texts texts;

    public Files(Rng rng) {
        this.rng = rng;
        this.numbers = new Numbers(rng);
        this.texts = new Texts(rng);
    }

    /** A random file extension without leading dot. */
    public String extension() {
        return rng.pick(EXTENSIONS);
    }

    /** A slug-based file name with a random extension. */
    public String fileName() {
        return fileName(extension());
    }

    /** A slug-based file name with the given extension (with or without leading dot). */
    public String fileName(String extension) {
        String ext = extension.startsWith(".") ? extension.substring(1) : extension;
        return texts.slug() + "." + ext;
    }

    /** Unix-style absolute path with 2–4 directory levels and a random file at the leaf. */
    public String path() {
        StringBuilder sb = new StringBuilder(rng.pick(ROOT_DIRS));
        int depth = numbers.intBetween(2, 4);
        for (int i = 0; i < depth; i++) {
            sb.append('/').append(texts.word());
        }
        sb.append('/').append(fileName());
        return sb.toString();
    }

    /** A directory path without a trailing file. */
    public String directory() {
        StringBuilder sb = new StringBuilder(rng.pick(ROOT_DIRS));
        int depth = numbers.intBetween(2, 5);
        for (int i = 0; i < depth; i++) {
            sb.append('/').append(texts.word());
        }
        return sb.toString();
    }

    /** A common MIME type drawn from a curated list. */
    public String mimeType() {
        List<String> values = List.copyOf(MIME_BY_EXT.values());
        return rng.pick(values);
    }

    /** The MIME type matching a given extension, or {@code application/octet-stream} if unknown. */
    public String mimeTypeFor(String extension) {
        String ext = extension.startsWith(".") ? extension.substring(1) : extension;
        return MIME_BY_EXT.getOrDefault(ext.toLowerCase(java.util.Locale.ROOT),
                "application/octet-stream");
    }

    /** A plausible byte size for a file: log-uniform between 1KB and 10MB. */
    public long fileSizeBytes() {
        // log-uniform: pick exponent uniformly in [10, 23], then a number in [2^e, 2^(e+1)).
        int exp = numbers.intBetween(10, 23);
        long base = 1L << exp;
        return base + numbers.longBetween(0, base - 1);
    }
}

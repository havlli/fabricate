package dev.havlli.fabricate.sink;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

abstract class DefaultSink<T extends Record> implements Sink<T> {

    @Override
    public final void write(Iterable<? extends T> rows, Path target) {
        Objects.requireNonNull(rows, "rows");
        Objects.requireNonNull(target, "target");
        writePath(rows.iterator(), target);
    }

    @Override
    public final void write(Stream<? extends T> rows, Path target) {
        Objects.requireNonNull(rows, "rows");
        Objects.requireNonNull(target, "target");
        writePath(rows.sequential().iterator(), target);
    }

    @Override
    public final void write(Iterable<? extends T> rows, Writer writer) {
        Objects.requireNonNull(rows, "rows");
        Objects.requireNonNull(writer, "writer");
        writeAndFlush(rows.iterator(), writer);
    }

    @Override
    public final void write(Stream<? extends T> rows, Writer writer) {
        Objects.requireNonNull(rows, "rows");
        Objects.requireNonNull(writer, "writer");
        writeAndFlush(rows.sequential().iterator(), writer);
    }

    @Override
    public final String writeToString(Iterable<? extends T> rows) {
        Objects.requireNonNull(rows, "rows");
        StringWriter writer = new StringWriter();
        write(rows, writer);
        return writer.toString();
    }

    @Override
    public final String writeToString(Stream<? extends T> rows) {
        Objects.requireNonNull(rows, "rows");
        StringWriter writer = new StringWriter();
        write(rows, writer);
        return writer.toString();
    }

    private void writePath(Iterator<? extends T> rows, Path target) {
        try (Writer writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writeRows(rows, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeAndFlush(Iterator<? extends T> rows, Writer writer) {
        try {
            writeRows(rows, writer);
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    abstract void writeRows(Iterator<? extends T> rows, Writer writer) throws IOException;
}

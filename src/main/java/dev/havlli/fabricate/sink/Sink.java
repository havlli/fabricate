package dev.havlli.fabricate.sink;

import java.io.Writer;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Writes records as a corpus in a chosen format.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances from
 * {@link Sinks} and must not implement this interface. Methods may be added in
 * future versions.
 *
 * <p>Rows are consumed once, in encounter order. Streams are consumed sequentially
 * without first collecting them and are not closed; the caller owns their lifecycle.
 * CSV and SQL derive columns from the first row, so all rows must have the same
 * record type. Empty input produces no output, including no CSV header.
 *
 * <p>All arguments must be non-null; null arguments throw {@link NullPointerException}
 * with the parameter name. I/O failures throw {@link java.io.UncheckedIOException}.
 *
 * @param <T> the record type
 */
public interface Sink<T extends Record> {

    /** Writes rows to a UTF-8 file, creating it or truncating an existing file. */
    void write(Iterable<? extends T> rows, Path target);

    /** Writes a stream to a UTF-8 file, creating it or truncating an existing file. */
    void write(Stream<? extends T> rows, Path target);

    /** Writes rows to the caller's writer, flushing it without closing it. */
    void write(Iterable<? extends T> rows, Writer writer);

    /** Writes a stream to the caller's writer, flushing it without closing it. */
    void write(Stream<? extends T> rows, Writer writer);

    /** Renders rows as a string. */
    String writeToString(Iterable<? extends T> rows);

    /** Renders a stream as a string. */
    String writeToString(Stream<? extends T> rows);
}

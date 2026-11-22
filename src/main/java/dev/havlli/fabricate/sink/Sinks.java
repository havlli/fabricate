package dev.havlli.fabricate.sink;

import java.nio.file.Path;
import dev.havlli.fabricate.internal.sink.DefaultCsvSink;
import dev.havlli.fabricate.internal.sink.DefaultJsonLinesSink;
import dev.havlli.fabricate.internal.sink.DefaultSqlSink;
import dev.havlli.fabricate.internal.sink.SinkEncoding;
import java.util.List;
import java.util.Objects;

/**
 * Factories for writing records as CSV, JSON Lines, or SQL.
 *
 * {@snippet :
 * Sinks.csv().write(people, Path.of("people.csv"));
 * Sinks.jsonLines().write(fab.person().stream().limit(10_000), Path.of("people.jsonl"));
 * Sinks.sql("person").batchSize(500).write(people, Path.of("people.sql"));
 * }
 */
public final class Sinks {

    private Sinks() {}

    /** Returns a CSV sink with a header derived from the first record's components. */
    public static <T extends Record> Sink<T> csv() {
        return new DefaultCsvSink<>();
    }

    /** Returns a sink that writes one JSON object per line. */
    public static <T extends Record> Sink<T> jsonLines() {
        return new DefaultJsonLinesSink<>();
    }

    /**
     * Returns a SQL sink that writes one INSERT statement per row by default.
     * The table name is used verbatim as a SQL identifier.
     *
     * @throws NullPointerException if {@code table} is null
     */
    public static <T extends Record> SqlSink<T> sql(String table) {
        return new DefaultSqlSink<>(Objects.requireNonNull(table, "table"), 1);
    }

    /**
     * Writes records as CSV with a header.
     * @deprecated Use {@link #csv()} and {@link Sink#write(Iterable, Path)}.
     */
    @Deprecated(since = "0.2.0", forRemoval = true)
    public static <T extends Record> void toCsv(List<T> rows, Path target) {
        Sinks.<T>csv().write(rows, target);
    }

    /**
     * Renders records as CSV with a header.
     * @deprecated Use {@link #csv()} and {@link Sink#writeToString(Iterable)}.
     */
    @Deprecated(since = "0.2.0", forRemoval = true)
    public static <T extends Record> String toCsvString(List<T> rows) {
        return Sinks.<T>csv().writeToString(rows);
    }

    /**
     * Writes one JSON object per line.
     * @deprecated Use {@link #jsonLines()} and {@link Sink#write(Iterable, Path)}.
     */
    @Deprecated(since = "0.2.0", forRemoval = true)
    public static <T extends Record> void toJsonLines(List<T> rows, Path target) {
        Sinks.<T>jsonLines().write(rows, target);
    }

    /**
     * Renders one JSON object per line.
     * @deprecated Use {@link #jsonLines()} and {@link Sink#writeToString(Iterable)}.
     */
    @Deprecated(since = "0.2.0", forRemoval = true)
    public static <T extends Record> String toJsonLinesString(List<T> rows) {
        return Sinks.<T>jsonLines().writeToString(rows);
    }

    /**
     * Writes one SQL INSERT statement per row.
     * @deprecated Use {@link #sql(String)} and {@link Sink#write(Iterable, Path)}.
     */
    @Deprecated(since = "0.2.0", forRemoval = true)
    public static <T extends Record> void toSqlInserts(List<T> rows, String table, Path target) {
        Sinks.<T>sql(table).write(rows, target);
    }

    /**
     * Renders one SQL INSERT statement per row.
     * @deprecated Use {@link #sql(String)} and {@link Sink#writeToString(Iterable)}.
     */
    @Deprecated(since = "0.2.0", forRemoval = true)
    public static <T extends Record> String toSqlInsertsString(List<T> rows, String table) {
        return Sinks.<T>sql(table).writeToString(rows);
    }

    /** Converts a single record to a JSON object string. */
    public static String toJsonString(Record row) {
        return SinkEncoding.toJsonString(Objects.requireNonNull(row, "row"));
    }
}

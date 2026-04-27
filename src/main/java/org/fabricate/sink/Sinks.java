package org.fabricate.sink;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.RecordComponent;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * One-line corpus dumpers for streams of records.
 *
 * Sinks turn a {@code List<? extends Record>} into a CSV/JSONL/SQL file or string.
 * They have no third-party deps — JSON encoding is a small built-in.
 *
 * {@snippet :
 * List<Person> people = fab.persons().list(1000);
 * Sinks.toCsv(people, Path.of("people.csv"));
 * Sinks.toJsonLines(people, Path.of("people.jsonl"));
 * Sinks.toSqlInserts(people, "person", Path.of("people.sql"));
 * }
 */
public final class Sinks {

    private Sinks() {}

    /** Write each record as one CSV row, with a header derived from the record components. */
    public static <T extends Record> void toCsv(List<T> rows, Path target) {
        if (rows.isEmpty()) {
            writeEmpty(target);
            return;
        }
        RecordComponent[] components = rows.get(0).getClass().getRecordComponents();
        try (Writer w = Files.newBufferedWriter(target, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            w.write(csvHeader(components));
            w.write('\n');
            for (T row : rows) {
                w.write(csvRow(row, components));
                w.write('\n');
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Render rows as a CSV string (header + data). */
    public static <T extends Record> String toCsvString(List<T> rows) {
        if (rows.isEmpty()) return "";
        RecordComponent[] components = rows.get(0).getClass().getRecordComponents();
        StringBuilder sb = new StringBuilder();
        sb.append(csvHeader(components)).append('\n');
        for (T row : rows) {
            sb.append(csvRow(row, components)).append('\n');
        }
        return sb.toString();
    }

    /** Write each record as one JSON object on its own line. */
    public static <T extends Record> void toJsonLines(List<T> rows, Path target) {
        try (Writer w = Files.newBufferedWriter(target, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (T row : rows) {
                w.write(toJsonString(row));
                w.write('\n');
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Render rows as JSONL (one JSON object per line). */
    public static <T extends Record> String toJsonLinesString(List<T> rows) {
        StringBuilder sb = new StringBuilder();
        for (T row : rows) {
            sb.append(toJsonString(row)).append('\n');
        }
        return sb.toString();
    }

    /** Write each record as a SQL {@code INSERT INTO <table> (...) VALUES (...)} statement. */
    public static <T extends Record> void toSqlInserts(List<T> rows, String table, Path target) {
        if (rows.isEmpty()) {
            writeEmpty(target);
            return;
        }
        RecordComponent[] components = rows.get(0).getClass().getRecordComponents();
        try (Writer w = Files.newBufferedWriter(target, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (T row : rows) {
                w.write(sqlInsert(row, table, components));
                w.write('\n');
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Render rows as a SQL INSERT script. */
    public static <T extends Record> String toSqlInsertsString(List<T> rows, String table) {
        if (rows.isEmpty()) return "";
        RecordComponent[] components = rows.get(0).getClass().getRecordComponents();
        StringBuilder sb = new StringBuilder();
        for (T row : rows) {
            sb.append(sqlInsert(row, table, components)).append('\n');
        }
        return sb.toString();
    }

    /** Convert a single record to a JSON object string. */
    public static String toJsonString(Record row) {
        RecordComponent[] components = row.getClass().getRecordComponents();
        Map<String, Object> values = new LinkedHashMap<>();
        for (RecordComponent c : components) {
            values.put(c.getName(), invoke(row, c));
        }
        return jsonObject(values);
    }

    private static String csvHeader(RecordComponent[] components) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < components.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(csvCell(components[i].getName()));
        }
        return sb.toString();
    }

    private static String csvRow(Record row, RecordComponent[] components) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < components.length; i++) {
            if (i > 0) sb.append(',');
            Object v = invoke(row, components[i]);
            sb.append(csvCell(v == null ? "" : String.valueOf(v)));
        }
        return sb.toString();
    }

    private static String csvCell(String s) {
        boolean needsQuote = s.indexOf(',') >= 0 || s.indexOf('"') >= 0
                || s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0;
        if (!needsQuote) return s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    private static String sqlInsert(Record row, String table, RecordComponent[] components) {
        StringBuilder sb = new StringBuilder("INSERT INTO ").append(table).append(" (");
        for (int i = 0; i < components.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(components[i].getName());
        }
        sb.append(") VALUES (");
        for (int i = 0; i < components.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(sqlLiteral(invoke(row, components[i])));
        }
        sb.append(");");
        return sb.toString();
    }

    private static String sqlLiteral(Object v) {
        if (v == null) return "NULL";
        if (v instanceof Number || v instanceof Boolean) {
            return String.valueOf(v).toUpperCase(Locale.ROOT).equals("TRUE") ? "TRUE"
                    : v.toString();
        }
        return "'" + v.toString().replace("'", "''") + "'";
    }

    private static String jsonObject(Map<String, Object> values) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> e : values.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append(jsonString(e.getKey())).append(':').append(jsonValue(e.getValue()));
        }
        return sb.append('}').toString();
    }

    private static String jsonValue(Object v) {
        if (v == null) return "null";
        if (v instanceof Number || v instanceof Boolean) return String.valueOf(v);
        if (v instanceof Record r) return toJsonString(r);
        if (v instanceof java.util.Optional<?> opt) {
            return opt.map(Sinks::jsonValue).orElse("null");
        }
        if (v instanceof Iterable<?> iter) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : iter) {
                if (!first) sb.append(',');
                first = false;
                sb.append(jsonValue(item));
            }
            return sb.append(']').toString();
        }
        return jsonString(v.toString());
    }

    private static String jsonString(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 2).append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) sb.append(String.format(Locale.ROOT, "\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.append('"').toString();
    }

    private static Object invoke(Record row, RecordComponent component) {
        try {
            return component.getAccessor().invoke(row);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to read record component " + component.getName(), e);
        }
    }

    private static void writeEmpty(Path target) {
        try {
            Files.writeString(target, "", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

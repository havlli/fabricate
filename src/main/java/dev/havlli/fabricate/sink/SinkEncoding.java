package dev.havlli.fabricate.sink;

import java.lang.reflect.RecordComponent;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/** Shared record encoding for the instance sinks and single-record JSON helper. */
final class SinkEncoding {

    private SinkEncoding() {}

    static String toJsonString(Record row) {
        RecordComponent[] components = row.getClass().getRecordComponents();
        Map<String, @Nullable Object> values = new LinkedHashMap<>();
        for (RecordComponent c : components) {
            values.put(c.getName(), invoke(row, c));
        }
        return jsonObject(values);
    }

    static String csvHeader(RecordComponent[] components) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < components.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(csvCell(components[i].getName()));
        }
        return sb.toString();
    }

    static String csvRow(Record row, RecordComponent[] components) {
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

    static String sqlPrefix(String table, RecordComponent[] components) {
        StringBuilder sb = new StringBuilder("INSERT INTO ").append(table).append(" (");
        for (int i = 0; i < components.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(components[i].getName());
        }
        return sb.append(") VALUES ").toString();
    }

    static String sqlRow(Record row, RecordComponent[] components) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < components.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(sqlLiteral(invoke(row, components[i])));
        }
        return sb.append(')').toString();
    }

    private static String sqlLiteral(@Nullable Object v) {
        if (v == null) return "NULL";
        if (v instanceof Number || v instanceof Boolean) {
            return String.valueOf(v).toUpperCase(Locale.ROOT).equals("TRUE") ? "TRUE"
                    : v.toString();
        }
        return "'" + v.toString().replace("'", "''") + "'";
    }

    private static String jsonObject(Map<String, @Nullable Object> values) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, @Nullable Object> e : values.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append(jsonString(e.getKey())).append(':').append(jsonValue(e.getValue()));
        }
        return sb.append('}').toString();
    }

    private static String jsonValue(@Nullable Object v) {
        if (v == null) return "null";
        if (v instanceof Number || v instanceof Boolean) return String.valueOf(v);
        if (v instanceof Record r) return toJsonString(r);
        if (v instanceof java.util.Optional<?> opt) {
            return opt.map(SinkEncoding::jsonValue).orElse("null");
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

    private static @Nullable Object invoke(Record row, RecordComponent component) {
        try {
            return component.getAccessor().invoke(row);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to read record component " + component.getName(), e);
        }
    }

}

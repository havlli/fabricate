package org.fabricate.sink;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SinksTest {

    record Row(int id, String name, boolean active) {}

    record WithSpecial(int id, String comment) {}

    record Nested(int id, Row row) {}

    record WithList(int id, List<String> tags) {}

    record WithOptional(int id, Optional<String> nickname) {}

    @Test
    void csv_emitsHeaderAndRows() {
        List<Row> rows = List.of(new Row(1, "Alice", true), new Row(2, "Bob", false));

        String csv = Sinks.toCsvString(rows);

        assertThat(csv).isEqualTo("""
                id,name,active
                1,Alice,true
                2,Bob,false
                """);
    }

    @Test
    void csv_quotesCellsContainingSpecialChars() {
        List<WithSpecial> rows = List.of(
                new WithSpecial(1, "hello, world"),
                new WithSpecial(2, "she said \"hi\""),
                new WithSpecial(3, "line\nbreak"));

        String csv = Sinks.toCsvString(rows);

        assertThat(csv).contains("\"hello, world\"");
        assertThat(csv).contains("\"she said \"\"hi\"\"\"");
        assertThat(csv).contains("\"line\nbreak\"");
    }

    @Test
    void csv_emptyListProducesEmptyOutput() {
        assertThat(Sinks.toCsvString(List.of())).isEmpty();
    }

    @Test
    void jsonl_emitsOneObjectPerLine() {
        List<Row> rows = List.of(new Row(1, "Alice", true), new Row(2, "Bob", false));

        String jsonl = Sinks.toJsonLinesString(rows);

        assertThat(jsonl).isEqualTo("""
                {"id":1,"name":"Alice","active":true}
                {"id":2,"name":"Bob","active":false}
                """);
    }

    @Test
    void json_escapesStrings() {
        Row row = new Row(1, "she said \"hi\"\nfoo\tbar", true);
        String json = Sinks.toJsonString(row);
        assertThat(json).contains("\\\"hi\\\"").contains("\\n").contains("\\t");
    }

    @Test
    void json_handlesNestedRecords() {
        Nested n = new Nested(99, new Row(1, "Alice", true));

        String json = Sinks.toJsonString(n);

        assertThat(json).isEqualTo(
                "{\"id\":99,\"row\":{\"id\":1,\"name\":\"Alice\",\"active\":true}}");
    }

    @Test
    void json_handlesLists() {
        WithList row = new WithList(1, List.of("a", "b", "c"));

        String json = Sinks.toJsonString(row);

        assertThat(json).isEqualTo("{\"id\":1,\"tags\":[\"a\",\"b\",\"c\"]}");
    }

    @Test
    void json_handlesOptionalPresentAndEmpty() {
        assertThat(Sinks.toJsonString(new WithOptional(1, Optional.of("ace"))))
                .isEqualTo("{\"id\":1,\"nickname\":\"ace\"}");
        assertThat(Sinks.toJsonString(new WithOptional(2, Optional.empty())))
                .isEqualTo("{\"id\":2,\"nickname\":null}");
    }

    @Test
    void sql_emitsOneInsertPerRow() {
        List<Row> rows = List.of(new Row(1, "Alice", true), new Row(2, "Bob", false));

        String sql = Sinks.toSqlInsertsString(rows, "people");

        assertThat(sql).isEqualTo("""
                INSERT INTO people (id, name, active) VALUES (1, 'Alice', TRUE);
                INSERT INTO people (id, name, active) VALUES (2, 'Bob', false);
                """);
    }

    @Test
    void sql_escapesSingleQuotes() {
        List<WithSpecial> rows = List.of(new WithSpecial(1, "O'Brien"));

        String sql = Sinks.toSqlInsertsString(rows, "people");

        assertThat(sql).contains("'O''Brien'");
    }

    @Test
    void csv_writesToFile(@TempDir Path tmp) throws IOException {
        Path file = tmp.resolve("rows.csv");

        Sinks.toCsv(List.of(new Row(1, "Alice", true)), file);

        assertThat(Files.readString(file)).startsWith("id,name,active\n1,Alice,true");
    }

    @Test
    void jsonl_writesToFile(@TempDir Path tmp) throws IOException {
        Path file = tmp.resolve("rows.jsonl");

        Sinks.toJsonLines(List.of(new Row(1, "Alice", true)), file);

        assertThat(Files.readString(file)).startsWith("{\"id\":1");
    }

    @Test
    void sql_writesToFile(@TempDir Path tmp) throws IOException {
        Path file = tmp.resolve("rows.sql");

        Sinks.toSqlInserts(List.of(new Row(1, "Alice", true)), "people", file);

        assertThat(Files.readString(file)).startsWith("INSERT INTO people");
    }
}

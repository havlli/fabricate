package dev.havlli.fabricate.sink;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class InstanceSinksTest {

    record Row(int id, String name, boolean active) {}
    record RichRow(String text, boolean active, @Nullable String missing,
                   Row nested, List<String> tags, Optional<String> optional) {}

    private static final List<Row> ROWS = List.of(
            new Row(1, "Alice", true), new Row(2, "Bob", false), new Row(3, "Zoë", true));

    private enum Format {
        CSV, JSONL, SQL;

        Sink<Record> sink() {
            return switch (this) {
                case CSV -> Sinks.csv();
                case JSONL -> Sinks.jsonLines();
                case SQL -> Sinks.sql("people");
            };
        }

        @SuppressWarnings("removal")
        String legacyString(List<Record> rows) {
            return switch (this) {
                case CSV -> Sinks.toCsvString(rows);
                case JSONL -> Sinks.toJsonLinesString(rows);
                case SQL -> Sinks.toSqlInsertsString(rows, "people");
            };
        }

        @SuppressWarnings("removal")
        void legacyPath(List<Record> rows, Path target) {
            switch (this) {
                case CSV -> Sinks.toCsv(rows, target);
                case JSONL -> Sinks.toJsonLines(rows, target);
                case SQL -> Sinks.toSqlInserts(rows, "people", target);
            }
        }
    }

    @Test
    void allFormatsAndTargetsMatchLegacyBytesForIterableAndStream(@TempDir Path tmp) throws IOException {
        List<Record> rich = List.of(
                new RichRow("comma, quote\" apostrophe' slash\\\n\r\t\b\f\u0001 Ω", true, null,
                        ROWS.getFirst(), List.of("a", "b"), Optional.of("present")),
                new RichRow("plain", false, null, ROWS.get(1), List.of(), Optional.empty()));
        for (Format format : Format.values()) {
            for (List<Record> rows : List.of(List.<Record>of(), List.<Record>copyOf(ROWS), rich)) {
                String expected = format.legacyString(rows);
                Path legacy = tmp.resolve("legacy");
                Files.writeString(legacy, "old contents that must be truncated");
                format.legacyPath(rows, legacy);
                assertThat(Files.readAllBytes(legacy)).isEqualTo(expected.getBytes(StandardCharsets.UTF_8));
                assertAllTargets(format.sink(), rows, expected, tmp);
            }
        }
    }

    private static <T extends Record> void assertAllTargets(
            Sink<? super T> sink, List<T> rows, String expected, Path tmp) throws IOException {
        // A plain Iterable that can only be traversed once, rather than a Collection.
        AtomicInteger traversals = new AtomicInteger();
        Iterable<T> iterable = () -> {
            assertThat(traversals.incrementAndGet()).isEqualTo(1);
            return rows.iterator();
        };
        assertThat(sink.writeToString(iterable)).isEqualTo(expected);
        assertThat(sink.writeToString(rows.parallelStream())).isEqualTo(expected);
        for (boolean stream : List.of(false, true)) {
            TrackingWriter writer = new TrackingWriter();
            if (stream) sink.write(rows.stream(), writer);
            else sink.write(rows, writer);
            assertThat(writer.toString()).isEqualTo(expected);
            assertThat(writer.flushed).isTrue();
            assertThat(writer.closed).isFalse();

            Path target = tmp.resolve("output");
            Files.deleteIfExists(target);
            if (stream) sink.write(rows.stream(), target);
            else sink.write(rows, target);
            assertThat(Files.readAllBytes(target)).isEqualTo(expected.getBytes(StandardCharsets.UTF_8));
            Files.writeString(target, "stale content".repeat(100));
            if (stream) sink.write(rows.stream(), target);
            else sink.write(rows, target);
            assertThat(Files.readAllBytes(target)).isEqualTo(expected.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Test
    void emptyInputProducesNoHeaderOrStatements(@TempDir Path tmp) throws IOException {
        for (Format format : Format.values()) {
            assertAllTargets(format.sink(), List.of(), "", tmp);
        }
        assertAllTargets(Sinks.sql("people").batchSize(2), List.of(), "", tmp);
    }

    @Test
    void sqlBatchSizeIsImmutableAndHandlesPartialExactAndOversizedBatches(@TempDir Path tmp)
            throws IOException {
        SqlSink<Row> original = Sinks.sql("people");
        SqlSink<Row> batched = original.batchSize(2);
        assertThat(batched).isNotSameAs(original);
        String firstTwo = "INSERT INTO people (id, name, active) VALUES (1, 'Alice', TRUE), (2, 'Bob', false);\n";
        String third = "INSERT INTO people (id, name, active) VALUES (3, 'Zoë', TRUE);\n";
        assertAllTargets(batched, ROWS, firstTwo + third, tmp);
        assertAllTargets(batched, ROWS.subList(0, 2), firstTwo, tmp);
        assertAllTargets(original.batchSize(Integer.MAX_VALUE), ROWS,
                "INSERT INTO people (id, name, active) VALUES (1, 'Alice', TRUE), (2, 'Bob', false), (3, 'Zoë', TRUE);\n",
                tmp);
        String singleStatements = """
                INSERT INTO people (id, name, active) VALUES (1, 'Alice', TRUE);
                INSERT INTO people (id, name, active) VALUES (2, 'Bob', false);
                INSERT INTO people (id, name, active) VALUES (3, 'Zoë', TRUE);
                """;
        assertThat(original.writeToString(ROWS)).isEqualTo(singleStatements);
        assertThat(batched.batchSize(1).writeToString(ROWS)).isEqualTo(singleStatements);
        assertThat(batched.writeToString(ROWS)).isEqualTo(firstTwo + third);
        for (int size : new int[] {0, -1, Integer.MIN_VALUE}) {
            assertThatIllegalArgumentException().isThrownBy(() -> original.batchSize(size));
        }
    }

    @Test
    void streamsAreWrittenIncrementallySequentiallyAndRemainCallerOwned() {
        List<Sink<Record>> sinks = List.of(Sinks.csv(), Sinks.jsonLines(), Sinks.sql("people"),
                Sinks.sql("people").batchSize(500));
        for (Sink<Record> sink : sinks) {
            TrackingWriter writer = new TrackingWriter();
            AtomicInteger pulled = new AtomicInteger();
            AtomicBoolean closed = new AtomicBoolean();
            Thread caller = Thread.currentThread();
            try (Stream<Row> rows = ROWS.parallelStream().peek(row -> {
                assertThat(Thread.currentThread()).isSameAs(caller);
                int previous = pulled.getAndIncrement();
                if (previous > 0) assertThat(writer.toString()).contains(ROWS.get(previous - 1).name());
            }).onClose(() -> closed.set(true))) {
                sink.write(rows, writer);
                assertThat(pulled).hasValue(ROWS.size());
                assertThat(closed).isFalse();
                assertThatThrownBy(rows::count).isInstanceOf(IllegalStateException.class);
            }
            assertThat(closed).isTrue();
        }
    }

    @Test
    void nullArgumentsNameTheirParameter(@TempDir Path tmp) {
        assertThatNullPointerException().isThrownBy(() -> Sinks.sql(null)).withMessage("table");
        for (Format format : Format.values()) {
            Sink<Record> sink = format.sink();
            Path target = tmp.resolve("unused");
            Writer writer = new StringWriter();
            assertThatNullPointerException().isThrownBy(() -> sink.write((Iterable<Record>) null, target))
                    .withMessage("rows");
            assertThatNullPointerException().isThrownBy(() -> sink.write((Stream<Record>) null, target))
                    .withMessage("rows");
            assertThatNullPointerException().isThrownBy(() -> sink.write((Iterable<Record>) null, writer))
                    .withMessage("rows");
            assertThatNullPointerException().isThrownBy(() -> sink.write((Stream<Record>) null, writer))
                    .withMessage("rows");
            assertThatNullPointerException().isThrownBy(() -> sink.writeToString((Iterable<Record>) null))
                    .withMessage("rows");
            assertThatNullPointerException().isThrownBy(() -> sink.writeToString((Stream<Record>) null))
                    .withMessage("rows");
            assertThatNullPointerException().isThrownBy(() -> sink.write(ROWS, (Path) null))
                    .withMessage("target");
            assertThatNullPointerException().isThrownBy(() -> sink.write(ROWS.stream(), (Path) null))
                    .withMessage("target");
            assertThatNullPointerException().isThrownBy(() -> sink.write(ROWS, (Writer) null))
                    .withMessage("writer");
            assertThatNullPointerException().isThrownBy(() -> sink.write(ROWS.stream(), (Writer) null))
                    .withMessage("writer");
            assertThat(target).doesNotExist();
        }
    }

    @Test
    void ioFailuresAreUncheckedAndDoNotCloseCallerWriter(@TempDir Path tmp) {
        for (Format format : Format.values()) {
            Sink<Record> sink = format.sink();
            for (boolean failOnFlush : List.of(false, true)) {
                AtomicBoolean closed = new AtomicBoolean();
                IOException failure = new IOException("deliberate failure");
                Writer writer = new Writer() {
                    @Override
                    public void write(char[] chars, int offset, int length) throws IOException {
                        if (!failOnFlush) throw failure;
                    }
                    @Override
                    public void flush() throws IOException {
                        throw failure;
                    }
                    @Override
                    public void close() {
                        closed.set(true);
                    }
                };
                assertThatThrownBy(() -> sink.write(ROWS, writer))
                        .isInstanceOf(UncheckedIOException.class).hasCause(failure);
                assertThat(closed).isFalse();
            }
            assertThatThrownBy(() -> sink.write(ROWS.stream(), tmp))
                    .isInstanceOf(UncheckedIOException.class);
        }
    }

    private static final class TrackingWriter extends StringWriter {
        boolean flushed;
        boolean closed;

        @Override
        public void flush() {
            flushed = true;
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}

package dev.havlli.fabricate.sink;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.RecordComponent;
import java.util.Iterator;

final class DefaultSqlSink<T extends Record> extends DefaultSink<T> implements SqlSink<T> {

    private final String table;
    private final int batchSize;

    DefaultSqlSink(String table, int batchSize) {
        this.table = table;
        this.batchSize = batchSize;
    }

    @Override
    public SqlSink<T> batchSize(int size) {
        if (size < 1) throw new IllegalArgumentException("size must be at least 1");
        return new DefaultSqlSink<>(table, size);
    }

    @Override
    void writeRows(Iterator<? extends T> rows, Writer writer) throws IOException {
        if (!rows.hasNext()) return;
        T row = rows.next();
        RecordComponent[] components = row.getClass().getRecordComponents();
        String prefix = SinkEncoding.sqlPrefix(table, components);
        int inBatch = 0;
        while (true) {
            writer.write(inBatch == 0 ? prefix : ", ");
            writer.write(SinkEncoding.sqlRow(row, components));
            if (++inBatch == batchSize) {
                writer.write(";\n");
                inBatch = 0;
            }
            if (!rows.hasNext()) break;
            row = rows.next();
        }
        if (inBatch != 0) writer.write(";\n");
    }
}

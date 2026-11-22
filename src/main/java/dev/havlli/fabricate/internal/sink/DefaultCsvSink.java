package dev.havlli.fabricate.internal.sink;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.RecordComponent;
import java.util.Iterator;

public final class DefaultCsvSink<T extends Record> extends DefaultSink<T> {

    @Override
    void writeRows(Iterator<? extends T> rows, Writer writer) throws IOException {
        if (!rows.hasNext()) return;
        T row = rows.next();
        RecordComponent[] components = row.getClass().getRecordComponents();
        writer.write(SinkEncoding.csvHeader(components));
        writer.write('\n');
        while (true) {
            writer.write(SinkEncoding.csvRow(row, components));
            writer.write('\n');
            if (!rows.hasNext()) return;
            row = rows.next();
        }
    }
}

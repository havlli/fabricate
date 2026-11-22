package dev.havlli.fabricate.internal.sink;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public final class DefaultJsonLinesSink<T extends Record> extends DefaultSink<T> {

    @Override
    void writeRows(Iterator<? extends T> rows, Writer writer) throws IOException {
        while (rows.hasNext()) {
            writer.write(SinkEncoding.toJsonString(rows.next()));
            writer.write('\n');
        }
    }
}

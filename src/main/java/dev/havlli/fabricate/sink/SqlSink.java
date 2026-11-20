package dev.havlli.fabricate.sink;

/**
 * Writes records as SQL INSERT statements, with configurable rows per statement.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances from
 * {@link Sinks#sql(String)} and must not implement this interface. Methods may be
 * added in future versions. Instances are immutable; the default batch size is one.
 *
 * @param <T> the record type
 */
public interface SqlSink<T extends Record> extends Sink<T> {

    /**
     * Returns a new sink with at most {@code size} rows per INSERT statement.
     * Each statement ends with a newline; the final batch may be shorter.
     *
     * @throws IllegalArgumentException if {@code size} is less than one
     */
    SqlSink<T> batchSize(int size);
}

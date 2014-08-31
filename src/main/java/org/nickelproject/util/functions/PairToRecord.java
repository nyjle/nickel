package org.nickelproject.util.functions;

import javax.annotation.Nonnull;

import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;

public final class PairToRecord<A, B> implements Function<Pair<A, B>, Record> {
    private final RecordDataType schema;
    
    public PairToRecord(final RecordDataType schema) {
        this.schema = schema;
    }
    
    @Override
    public Record apply(@Nonnull final Pair<A, B> input) {
        return Record.of(schema, input.getA(), input.getB());
    }
}

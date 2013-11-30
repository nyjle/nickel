package org.nickelproject.util.functions;

import org.nickelproject.nickel.types.Record;

import com.google.common.base.Function;

public class GetFields implements Function<Record, Object[]> {

    @Override
    public Object[] apply(final Record record) {
        return record.getFields();
    }
}

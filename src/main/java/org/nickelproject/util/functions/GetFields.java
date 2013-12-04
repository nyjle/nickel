package org.nickelproject.util.functions;

import javax.annotation.Nonnull;

import org.nickelproject.nickel.types.Record;

import com.google.common.base.Function;

public final class GetFields implements Function<Record, Object[]> {

    @Override
    public Object[] apply(@Nonnull final Record record) {
        return record.getFields();
    }
}

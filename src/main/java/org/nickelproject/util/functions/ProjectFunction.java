package org.nickelproject.util.functions;

import java.io.Serializable;

import com.google.common.base.Function;

public final class ProjectFunction implements Function<Object[], Object[]>, Serializable {
    private static final long serialVersionUID = 1L;
    private final int[] fields;
    
    public ProjectFunction(final int... fields) {
        this.fields = fields;
    }

    @Override
    public Object[] apply(final Object[] pVal) {
        final Object[] array = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            array[i] = pVal[fields[i]];
        }
        return array;
    }
}

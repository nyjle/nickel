package org.nickelproject.util.functions;

import javax.annotation.Nonnull;

import com.google.common.base.Function;

public final class ArrayElementFunction<T> implements Function<Object[], T> {
    private final int index;
    
    public ArrayElementFunction(final int index) {
        this.index = index;
    }
    
    @Override
    public T apply(@Nonnull final Object[] input) {
        return (T) input[index];
    }
}

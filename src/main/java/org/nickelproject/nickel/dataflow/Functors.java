package org.nickelproject.nickel.dataflow;

import com.google.common.base.Function;

public final class Functors {

    private Functors() {
        // Prevents construction
    }
    
    public static <S, T> Function<S, T> constant(final T value) {
        return new Function<S, T>() {
            @Override
            public T apply(final S input) {
                return value;
            }
        };
    }
}

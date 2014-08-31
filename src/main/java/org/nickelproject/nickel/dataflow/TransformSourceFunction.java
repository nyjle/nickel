package org.nickelproject.nickel.dataflow;

import javax.annotation.Nonnull;

import com.google.common.base.Function;

public class TransformSourceFunction<S, T> implements Function<Source<S>, Source<T>> {
    private final Function<S, T> transform;
    
    public TransformSourceFunction(final Function<S, T> transform) {
        this.transform = transform;
    }
    
    @Override
    public Source<T> apply(@Nonnull Source<S> input) {
        return Sources.transform(input, transform);
    }
}

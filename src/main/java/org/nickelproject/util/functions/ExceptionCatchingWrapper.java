package org.nickelproject.util.functions;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;

public final class ExceptionCatchingWrapper<F, T> implements Function<F, Pair<T, Exception>>, Serializable {
    private final Function<F, T> function;

    private ExceptionCatchingWrapper(final Function<F, T> function) {
        this.function = function;
    }
    
    public static <F, T> Function<F, Pair<T, Exception>> wrap(final Function<F, T> function) {
        return new ExceptionCatchingWrapper<F, T>(function);
    }
    
    @Override
    @Nullable
    public Pair<T, Exception> apply(@Nullable final F input) {
        Exception exception = null;
        T result = null;
        try {
            result = function.apply(input);
        } catch (Exception e) {
            exception = e;
        }
        return Pair.of(result, exception);
    }
}

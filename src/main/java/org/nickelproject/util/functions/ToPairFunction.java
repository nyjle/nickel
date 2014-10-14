package org.nickelproject.util.functions;

import java.io.Serializable;

import javax.annotation.Nonnull;

import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;

public final class ToPairFunction<T> implements Function<T, Pair<T, T>>, Serializable {

    @Override
    public Pair<T, T> apply(@Nonnull final T input) {
        return Pair.of(input, input);
    }
}

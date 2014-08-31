package org.nickelproject.util.functions;

import javax.annotation.Nonnull;

import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;

public class ToPairFunction<T> implements Function<T, Pair<T, T>>{

    @Override
    public Pair<T, T> apply(@Nonnull T input) {
        return Pair.of(input, input);
    }
}

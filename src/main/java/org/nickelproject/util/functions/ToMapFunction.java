package org.nickelproject.util.functions;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;

import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;

public final class ToMapFunction<T, S> implements Function<Pair<T, S>, Map<T, S>> {

    @Override
    public Map<T, S> apply(@Nonnull final Pair<T, S> pair) {
        return Collections.singletonMap(pair.getA(), pair.getB());
    }
}

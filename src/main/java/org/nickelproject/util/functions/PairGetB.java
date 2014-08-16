package org.nickelproject.util.functions;

import javax.annotation.Nullable;

import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;

public class PairGetB<A, B> implements Function<Pair<A, B>, B> {

    @Override @Nullable
    public B apply(@Nullable Pair<A, B> input) {
        return input.getB();
    }
}

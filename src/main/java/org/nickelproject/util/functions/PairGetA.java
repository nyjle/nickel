package org.nickelproject.util.functions;

import javax.annotation.Nullable;

import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;

public class PairGetA<A, B> implements Function<Pair<A, B>, A> {

    @Override @Nullable
    public A apply(@Nullable Pair<A, B> input) {
        return input.getA();
    }
}

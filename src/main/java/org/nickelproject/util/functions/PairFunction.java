package org.nickelproject.util.functions;

import org.nickelproject.util.tuple.Pair;
import com.google.common.base.Function;

public final class PairFunction<S, U, T> implements Function<S, Pair<U, T>> {
    private final Pair<Function<S, U>, Function<S, T>> functionPair;
    
    private PairFunction(final Pair<Function<S, U>, Function<S, T>> functionPair) {
        this.functionPair = functionPair;
    }
    
    public static <S, U, T> PairFunction<S, U, T> of(final Function<S, U> functionA,
            final Function<S, T> functionB) {
        return new PairFunction<S, U, T>(Pair.of(functionA, functionB));
    }
    
    @Override
    public Pair<U, T> apply(final S input) {
        return Pair.of(functionPair.getA().apply(input), functionPair.getB().apply(input));
    }
}

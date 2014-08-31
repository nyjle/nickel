package org.nickelproject.util.reducers;

import org.nickelproject.nickel.dataflow.Reducer;
import org.nickelproject.nickel.dataflow.Reductor;
import org.nickelproject.util.tuple.Pair;

public final class PairReducer<A, B> implements Reducer<Pair<A, B>> {
    private final Reducer<A> reducerA;
    private final Reducer<B> reducerB;
    
    public PairReducer(final Reducer<A> reducerA, final Reducer<B> reducerB) {
        this.reducerA = reducerA;
        this.reducerB = reducerB;
    }
    
    @Override
    public Reductor<Pair<A, B>, Pair<A, B>> reductor() {
        return new PairReductor<A, B>(reducerA.reductor(), reducerB.reductor());
    }
    
    private static final class PairReductor<A, B> implements Reductor<Pair<A, B>, Pair<A, B>> {
        private final Reductor<A, A> reductorA;
        private final Reductor<B, B> reductorB;
        
        PairReductor(final Reductor<A, A> reductorA, final Reductor<B, B> reductorB) {
            this.reductorA = reductorA;
            this.reductorB = reductorB;
        }
        
        @Override
        public void collect(final Pair<A, B> val) {
            reductorA.collect(val.getA());
            reductorB.collect(val.getB());
        }

        @Override
        public Pair<A, B> reduce() {
            return Pair.of(reductorA.reduce(), reductorB.reduce());
        }
    }
}

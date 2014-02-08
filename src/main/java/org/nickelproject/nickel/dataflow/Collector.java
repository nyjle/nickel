package org.nickelproject.nickel.dataflow;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public final class Collector<S, T, V> implements CollectorInterface<S, V> {
    private static final long    serialVersionUID = 1L;
    private final Function<S, T> preFunctor;
    private final Reducer<T>  reducer;
    private final Function<T, V> postFunctor;

    private Collector(final Function<S, T> preFunctor, final Reducer<T> reducer,
            final Function<T, V> postFunctor) {
        this.preFunctor = preFunctor;
        this.reducer = reducer;
        this.postFunctor = postFunctor;
    }

    public static <S, T, V> Collector<S, T, V> create(final Function<S, T> preFunctor,
            final Reducer<T> reducer,
            final Function<T, V> postFunctor) {
        return new Collector<S, T, V>(preFunctor, reducer, postFunctor);
    }
    
    public static <S, T, X, Y, V> Collector<S, X, V> create(final Function<S, T> preFunctor, 
            final Collector<T, X, Y> collector, 
            final Function<Y, V> postFunctor) {
        final Function<S, X> newPreFunction = Functions.compose(collector.preFunctor, preFunctor);
        final Function<X, V> newPostFunction = Functions.compose(postFunctor, collector.postFunctor);
        return new Collector<S, X, V>(newPreFunction, collector.reducer, newPostFunction);
    }

    @Override
    public Reductor<S, V> reductor() {
        return new ComposingReductor<S, T, V>(preFunctor, reducer.reductor(), postFunctor);
    }

    private static final class ComposingReductor<S, T, V> implements Reductor<S, V> {
        private final Function<S, T> preFunction;
        private final Function<T, V> postFunction;
        private final Reductor<T, T> reductor;

        ComposingReductor(final Function<S, T> preFunction, final Reductor<T, T> reductor,
                final Function<T, V> postFunction) {
            this.preFunction = preFunction;
            this.postFunction = postFunction;
            this.reductor = reductor;
        }
        
        @Override
        public void collect(final S pVal) {
            reductor.collect(preFunction.apply(pVal));
        }

        @Override
        public V reduce() {
            return postFunction.apply(reductor.reduce());
        }
    }
}

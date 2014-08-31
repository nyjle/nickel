package org.nickelproject.nickel.dataflow;

import java.util.Map;

import org.nickelproject.util.functions.PairFunction;
import org.nickelproject.util.functions.ToPairFunction;
import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * A wrapper to hide the irrelevant intermediate class in CollectorBase.
 *
 * @param <T>
 * @param <U>
 */
public final class Collector<T, U> implements CollectorInterface<T, U> {
    private static final long serialVersionUID = 1L;
    private final CollectorBase<T, ?, U> collectorBase;

    private Collector(final CollectorBase<T, ?, U> collectorBase) {
        this.collectorBase = collectorBase;
    }
    
    public static <S, T, V> Collector<S, V> create(final Function<S, T> preFunctor,
            final Reducer<T> reducer,
            final Function<T, V> postFunctor) {
        return new Collector<S, V>(new CollectorBase<S, T, V>(preFunctor, reducer, postFunctor));
    }
    
    public static <S, T> Collector<S, T> create(final Function<S, T> preFunction, final Reducer<T> reducer) {
        return create(preFunction, reducer, Functions.<T>identity());
    }
      
    public static <S, T, X, Y, V> Collector<S, V> create(final Function<S, T> preFunctor, 
            final Collector<T, Y> collector, 
            final Function<Y, V> postFunctor) {
        @SuppressWarnings("unchecked")
        final CollectorBase<T, X, Y> wrapped = (CollectorBase<T, X, Y>) collector.collectorBase;
        final Function<S, X> newPreFunction = Functions.compose(wrapped.preFunctor, preFunctor);
        final Function<X, V> newPostFunction = Functions.compose(postFunctor, wrapped.postFunctor);
        return new Collector<S, V>(
                new CollectorBase<S, X, V>(newPreFunction, wrapped.reducer, newPostFunction));
    }
    
    public static <S, T, V> Collector<S, V> create(final Function<S, T> preFunction, 
            final Collector<T, V> collector) {
        return create(preFunction, collector, Functions.<V>identity());
    }
    
    public static <T, V, W> Collector<T, W> create( 
            final Collector<T, V> collector, final Function<V, W> postFunction) {
        return create(Functions.<T>identity(), collector, postFunction);
    }
    
    public static <I, K, T, V> Collector<I, Source<Pair<K, V>>> 
                    groupBy(final Function<I, K> keyFunction, final Collector<I, V> aggregator) {
        final Reducer<Map<K, T>> mapReducer = 
                                    MapMergingReducer.create((Reducer<T>) aggregator.collectorBase.reducer);
        final Function<I, Pair<I, I>> toPairFunction = new ToPairFunction<I>();
        final Function<Pair<I, I>, Pair<K, T>> pairFunction = 
                PairFunction.of(keyFunction, (Function<I, T>) aggregator.collectorBase.preFunctor);
        final Function<Pair<K, T>, Map<K, T>> toMap = new ToMapFunction();
        return Collector.create(
                Functions.compose(toMap, 
                        Functions.compose(pairFunction, toPairFunction)),
                mapReducer, Functions.compose(
                        new TransformSourceFunction(
                                PairFunction.of(Functions.<K>identity(), 
                                        (Function<T, V>) aggregator.collectorBase.postFunctor)),
                        new MapToPairSourceFunction<K, T>()));
    }
      
    @Override
    public Reductor<T, U> reductor() {
        return collectorBase.reductor();
    }
    
    private static final class CollectorBase<S, T, V> implements CollectorInterface<S, V> {
        private static final long    serialVersionUID = 1L;
        private final Function<S, T> preFunctor;
        private final Reducer<T>  reducer;
        private final Function<T, V> postFunctor;

        private CollectorBase(final Function<S, T> preFunctor, final Reducer<T> reducer,
                final Function<T, V> postFunctor) {
            this.preFunctor = preFunctor;
            this.reducer = reducer;
            this.postFunctor = postFunctor;
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
}

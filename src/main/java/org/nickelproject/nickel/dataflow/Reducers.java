package org.nickelproject.nickel.dataflow;

import com.google.common.base.Function;

public class Reducers {
    public static <S, T, U, V> Reducer<S, V> compose(final Function<S, T> preFunctor, final Reducer<T, U> reducer,
            final Function<U, V> postFunctor) {
        return new ComposingReducer<S, T, U, V>(preFunctor, reducer, postFunctor);
    }

    private static final class ComposingReducer<S, T, U, V> implements Reducer<S, V> {
        private static final long    serialVersionUID = 1L;
        private final Function<S, T> preFunctor;
        private final Reducer<T, U>  reducer;
        private final Function<U, V> postFunctor;

        private ComposingReducer(final Function<S, T> preFunctor, final Reducer<T, U> reducer,
                final Function<U, V> postFunctor) {
            this.preFunctor = preFunctor;
            this.reducer = reducer;
            this.postFunctor = postFunctor;
        }

        @Override
        public Reductor<S, V> reductor() {
            return new ComposingReductor();
        }

        private class ComposingReductor implements Reductor<S, V> {
            private final Reductor<T, U> reductor = reducer.reductor();

            @Override
            public void collect(final S pVal) {
                reductor.collect(preFunctor.apply(pVal));
            }

            @Override
            public V reduce() {
                return postFunctor.apply(reductor.reduce());
            }
        }
    }

}

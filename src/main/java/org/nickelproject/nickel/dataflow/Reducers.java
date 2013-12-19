/*
 * Copyright (c) 2013 Nigel Duffy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nickelproject.nickel.dataflow;

import com.google.common.base.Function;

public final class Reducers {
    
    private Reducers() {
        // Prevents construction
    }
    
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

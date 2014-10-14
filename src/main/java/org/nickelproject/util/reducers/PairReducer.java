/*
 * Copyright (c) 2013, 2014 Nigel Duffy
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

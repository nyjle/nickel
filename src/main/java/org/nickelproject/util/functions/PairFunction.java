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
package org.nickelproject.util.functions;

import org.nickelproject.util.tuple.Pair;
import com.google.common.base.Function;

public final class PairFunction<S, T, U, V> implements Function<Pair<S, T>, Pair<U, V>> {
    private final Function<S, U> functionA; 
    private final Function<T, V> functionB;
    
    private PairFunction(final Function<S, U> functionA, final Function<T, V> functionB) {
        this.functionA = functionA;
        this.functionB = functionB;
    }
    
    public static <S, T, U, V> PairFunction<S, T, U, V> of(final Function<S, U> functionA,
            final Function<T, V> functionB) {
        return new PairFunction<S, T, U, V>(functionA, functionB);
    }
    
    @Override
    public Pair<U, V> apply(final Pair<S ,T> input) {
        return Pair.of(functionA.apply(input.getA()), functionB.apply(input.getB()));
    }
}

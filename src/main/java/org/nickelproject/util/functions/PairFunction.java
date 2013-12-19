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

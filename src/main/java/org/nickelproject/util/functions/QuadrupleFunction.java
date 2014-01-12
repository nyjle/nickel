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

import org.nickelproject.util.tuple.Quadruple;

import com.google.common.base.Function;

public final class QuadrupleFunction<S, A, B, C, D> implements Function<S[], Quadruple<A, B, C, D>> {
    private final Function<S[], A> functionA;
    private final Function<S[], B> functionB;
    private final Function<S[], C> functionC;
    private final Function<S[], D> functionD;

    private QuadrupleFunction(
            final Function<S[], A> functionA,
            final Function<S[], B> functionB,
            final Function<S[], C> functionC,
            final Function<S[], D> functionD) {
        this.functionA = functionA;
        this.functionB = functionB;
        this.functionC = functionC;
        this.functionD = functionD;
    }

    public static <S, A, B, C, D> QuadrupleFunction<S, A, B, C, D> of(
            final Function<S[], A> functionA, 
            final Function<S[], B> functionB, 
            final Function<S[], C> functionC,
            final Function<S[], D> functionD) {
        return new QuadrupleFunction<S, A, B, C, D>(functionA, functionB, functionC, functionD);
    }
    
    @Override
    public Quadruple<A, B, C, D> apply(final S[] pFrom) {
        return Quadruple.of(functionA.apply(pFrom), functionB.apply(pFrom), functionC.apply(pFrom),
                functionD.apply(pFrom));
    }
}

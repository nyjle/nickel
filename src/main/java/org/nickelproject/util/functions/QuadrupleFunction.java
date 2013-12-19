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

public final class QuadrupleFunction implements
        Function<Object[], Quadruple<Double, ? extends Object, Integer, ? extends Object>> {
    private final Function<Object[], Double>           mWeightFunctor;
    private final Function<Object[], ? extends Object> mLabelFunctor;
    private final Function<Object[], Integer>          mWhichSetFunctor;
    private final Function<Object[], ? extends Object> mPredictionFunctor;

    public QuadrupleFunction(final Function<Object[], Double> pWeightFunctor,
            final Function<Object[], ? extends Object> pLabelFunctor,
            final Function<Object[], Integer> pWhichSetFunctor,
            final Function<Object[], ? extends Object> pPredictionFunctor) {
        mWeightFunctor = pWeightFunctor;
        mLabelFunctor = pLabelFunctor;
        mWhichSetFunctor = pWhichSetFunctor;
        mPredictionFunctor = pPredictionFunctor;
    }

    @Override
    public Quadruple<Double, ? extends Object, Integer, ? extends Object> apply(final Object[] pFrom) {
        return Quadruple.of(mWeightFunctor.apply(pFrom), mLabelFunctor.apply(pFrom), mWhichSetFunctor.apply(pFrom),
                mPredictionFunctor.apply(pFrom));
    }
}

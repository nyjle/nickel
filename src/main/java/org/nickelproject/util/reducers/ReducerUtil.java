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
package org.nickelproject.util.reducers;

import java.util.List;

import javax.annotation.Nonnull;

import org.nickelproject.nickel.dataflow.Collector;
import org.nickelproject.nickel.dataflow.Reducer;
import org.nickelproject.util.functions.FunctionUtil;
import org.nickelproject.util.functions.PairFunction;
import org.nickelproject.util.functions.ToListFunction;
import org.nickelproject.util.functions.ToPairFunction;
import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public final class ReducerUtil {
    
    private ReducerUtil() {
        // Prevents construction
    }
    
    public static <T> Collector<T, Integer> count() {
        return Collector.create(FunctionUtil.<T, Integer>constant(1), new IntegerSumReducer());
    }
    
    public static Collector<Double, Double> average() {
        final Reducer<Pair<Integer, Double>> reducer =
                new PairReducer<Integer, Double>(new IntegerSumReducer(), new DoubleSumReducer());
        final Function<Pair<Double, Double>, Pair<Integer, Double>> function = 
                PairFunction.of(FunctionUtil.<Double, Integer>constant(1), Functions.<Double>identity());
        final Function<Pair<Integer, Double>, Double> divide = new Function<Pair<Integer, Double>, Double>() {
            @Override
            public Double apply(@Nonnull final Pair<Integer, Double> from) {
                return from.getB() / from.getA();
            }
        };
        return Collector.create(Functions.compose(function, new ToPairFunction<Double>()), reducer, divide);
    }
    
    public static <T> Collector<T, List<T>> toList() {
        return Collector.create(new ToListFunction<T>(), new MergeListReducer<T>());
    }
}

/*
 * Copyright (c) 2013 Numerate, Inc
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

/**
 * Collects integer values and calculates their sum.
 */
public final class DoubleSumReducer implements Reducer<Double> {
    private static final long serialVersionUID = 1L;
    private final double initial;
    
    public DoubleSumReducer() {
        this(0);
    }
    
    public DoubleSumReducer(final double initial) {
        this.initial = initial;
    }
    
    @Override
    public Reductor<Double, Double> reductor() {
        return new DoubleSumReductor(initial);
    }
    
    protected static final class DoubleSumReductor implements Reductor<Double, Double> {
        private double sum;

        public DoubleSumReductor(final double initial) {
            sum = initial;
        }
        
        @Override
        public void collect(final Double from) {
            sum += from.doubleValue();
        }
        
        @Override
        public Double reduce() {
            return Double.valueOf(sum);
        }
    }
}

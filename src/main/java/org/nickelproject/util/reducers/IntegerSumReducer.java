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
public final class IntegerSumReducer implements Reducer<Integer> {
    private static final long serialVersionUID = 1L;
    private final int mInitial;
    
    public IntegerSumReducer() {
        this(0);
    }
    
    public IntegerSumReducer(final int pInitial) {
        mInitial = pInitial;
    }
    
    @Override
    public Reductor<Integer, Integer> reductor() {
        return new IntegerSumReductor(mInitial);
    }
    
    protected static final class IntegerSumReductor implements Reductor<Integer, Integer> {
        private int mSum;

        public IntegerSumReductor(final int pInitial) {
            mSum = pInitial;
        }
        
        @Override
        public void collect(final Integer pFrom) {
            mSum += pFrom.intValue();
        }
        
        @Override
        public Integer reduce() {
            return Integer.valueOf(mSum);
        }
    }
}

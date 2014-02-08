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

import org.nickelproject.nickel.dataflow.Reducer;
import org.nickelproject.nickel.dataflow.Reductor;

/**
 * @author Nigel
 */
public final class MaxReducer<T extends Comparable<T>> implements Reducer<T> {
    private static final long serialVersionUID = 1L;

    @Override
    public Reductor<T, T> reductor() {
        return new MaxReductor();
    }

    private class MaxReductor implements Reductor<T, T> {
        private T mMax = null;

        @Override
        public void collect(final T pVal) {
            mMax = mMax == null ? pVal : pVal.compareTo(mMax) > 0 ? pVal : mMax;
        }

        @Override
        public T reduce() {
            return mMax;
        }
    }
}

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

import java.util.List;

import org.nickelproject.nickel.dataflow.Reducer;
import org.nickelproject.nickel.dataflow.Reductor;

import com.google.common.collect.Lists;

public final class ToListReducer<T> implements Reducer<T, List<T>> {
    private static final long serialVersionUID = 1L;

    @Override
    public Reductor<T, List<T>> reductor() {
        return new ToListReductor<T>();
    }

    private static class ToListReductor<T> implements Reductor<T, List<T>> {
        private final List<T> mList = Lists.newArrayList();

        @Override
        public void collect(final T pVal) {
            mList.add(pVal);
        }

        @Override
        public List<T> reduce() {
            return mList;
        }
    }
}

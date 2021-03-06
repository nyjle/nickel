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
package org.nickelproject.nickel.dataflow;

import java.util.Map;
import java.util.Map.Entry;


import com.google.common.collect.Maps;

public final class MapMergingReducer<T, U> implements Reducer<Map<T, U>> {
    private static final long   serialVersionUID = 1L;
    private final Reducer<U> reducer;

    private MapMergingReducer(final Reducer<U> reducer) {
        this.reducer = reducer;
    }

    public static <T, U> Reducer<Map<T, U>> create(final Reducer<U> reducer) {
        return new MapMergingReducer<T, U>(reducer);
    }

    @Override
    public Reductor<Map<T, U>, Map<T, U>> reductor() {
        return new MapReductor();
    }

    private class MapReductor implements Reductor<Map<T, U>, Map<T, U>> {
        private final Map<T, Reductor<U, U>> reductorMap = Maps.newHashMap();

        @Override
        public void collect(final Map<T, U> pVal) {
            for (final Entry<T, U> entry : pVal.entrySet()) {
                Reductor<U, U> reductor = reductorMap.get(entry.getKey());
                if (reductor == null) {
                    reductor = reducer.reductor();
                    reductorMap.put(entry.getKey(), reductor);
                }
                reductor.collect(pVal.get(entry.getKey()));
            }
        }

        @Override
        public Map<T, U> reduce() {
            final Map<T, U> retVal = Maps.newHashMap();
            for (final T key : reductorMap.keySet()) {
                retVal.put(key, reductorMap.get(key).reduce());
            }
            return retVal;
        }
    }
}

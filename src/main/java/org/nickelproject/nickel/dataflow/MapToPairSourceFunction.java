/*
 * Copyright (c) 2013, 2014 Nigel Duffy
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

import javax.annotation.Nonnull;

import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;

public final class MapToPairSourceFunction<K, V> implements Function<Map<K, V>, Source<Pair<K, V>>> {

    @Override
    public Source<Pair<K, V>> apply(@Nonnull final Map<K, V> input) {
        return Sources.transform(
                Sources.from(input.entrySet()), 
                    new Function<Map.Entry<K, V>, Pair<K, V>>() {
    
                        @Override
                        public Pair<K, V> apply(@Nonnull final Entry<K, V> input) {
                            return Pair.of(input.getKey(), input.getValue());
                        }                    
                    }
               );
    }
}

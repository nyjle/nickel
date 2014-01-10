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

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public final class CrossProduct {
    
    private CrossProduct() {
        // Prevents construction
    }
    
    public static Source<List<Object>> crossProduct(final List<Source<? extends Object>> sources) {
        final Source<? extends Object> firstSource = sources.get(0);
        Source<List<Object>> retVal = Sources.transform(firstSource, new Function<Object, List<Object>>() {
            @Override
            public List<Object> apply(final Object input) {
                return Lists.newArrayList(input);
            }                    
        });
        for (int i = 1; i < sources.size(); i++) {
            retVal = Sources.concat(Sources.transform(sources.get(i), new AddToList(retVal)));
        }
        return retVal;
    }
    
    private static final class AddToList implements Function<Object, Source<List<Object>>> {
        private final Source<List<Object>> source;

        public AddToList(final Source<List<Object>> source) {
            this.source = source;
        }
        
        @Override
        public Source<List<Object>> apply(final Object input) {
            return Sources.transform(source, new Function<List<Object>, List<Object>>() {
                @Override
                public List<Object> apply(final List<Object> list) {
                    list.add(input);
                    return list;
                }
            });
        }        
    }
}

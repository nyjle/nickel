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
package org.nickelproject.util.sources;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;

import com.google.common.base.Function;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;

public final class Sequences {

    private Sequences() {
        // Prevents Construction
    }
    
    public static Source<Integer> integer(final int min, final int max) {
        return Sources.from(ContiguousSet.create(Range.closedOpen(min, max), DiscreteDomain.integers()));
    }
    
    public static Source<Integer> integer(final int min, final int max, final int step) {
        return Sources.transform(integer(0, (max-min) / step), new Function<Integer, Integer>() {
                @Override
                public Integer apply(final Integer input) {
                    return input * step + min;
                }
            });
    }
}

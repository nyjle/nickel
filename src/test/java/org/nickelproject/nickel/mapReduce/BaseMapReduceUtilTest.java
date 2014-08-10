/*
 * Copyright (c) 2013 Numerate, Inc
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
package org.nickelproject.nickel.mapReduce;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nickelproject.nickel.dataflow.MapReduceUtil;
import org.nickelproject.util.reducers.IntegerSumReducer;
import org.nickelproject.util.sources.Sequences;
import org.nickelproject.util.testUtil.UnitAnnotation;

import com.google.common.base.Function;

/**
 * Tests for {@link MapReduceUtil}.
 */
@UnitAnnotation
public abstract class BaseMapReduceUtilTest {
    protected abstract Mapper getMapper();

    @Test
    public final void testBasic() {
        final int kNumElements = 1000;

        final Integer vResult = MapReduceUtil.mapReduce(Sequences.integer(0, kNumElements),
                new MultiplyFunction(), new IntegerSumReducer(), getMapper());
        assertEquals((kNumElements * (kNumElements - 1)) / 2 * MultiplyFunction.kFactor, vResult.intValue());
    }

    private static class MultiplyFunction implements Function<Integer, Integer> {
        static final int kFactor = 10;

        @Override
        public Integer apply(final Integer pFrom) {
            return Integer.valueOf(pFrom.intValue() * kFactor);
        }
    }
}

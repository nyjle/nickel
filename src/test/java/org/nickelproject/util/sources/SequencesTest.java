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

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.sources.SourceTestUtil;
import org.nickelproject.util.testUtil.UnitAnnotation;

@UnitAnnotation
public final class SequencesTest {
    private static final int start = 1;
    private static final int end = 7;
    private static final int step = 2;

    @Test
    public void testUnitIncrement() {
        doTest(start, end, 1);
    }

    @Test
    public void testNonUnitIncrement() {
        doTest(start, end, step);
    }
    
    private void doTest(final int pStart, final int pEnd, final int pStep) {
        final Source<Integer> sequence = Sequences.integer(pStart, pEnd, pStep);
        int count = pStart;
        for (final Integer val : SourceTestUtil.toIterable(sequence)) {
             Assert.assertEquals(count, val.intValue());
             count += pStep;
        }
        Assert.assertTrue(count >= pEnd);
    }
}

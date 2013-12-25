package org.nickelproject.util.sources;

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.nickel.dataflow.Source;

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
    
    private void doTest(final int start, final int end, final int step) {
        final Source<Integer> sequence = Sequences.integer(start, end, step);
        int count = start;
        for (final Integer val : sequence) {
             Assert.assertEquals(count, val.intValue());
             count += step;
        }
        Assert.assertTrue(count >= end);
    }
}

package org.nickelproject.util.sources;

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.suites.UnitAnnotation;

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
        for (final Integer val : sequence) {
             Assert.assertEquals(count, val.intValue());
             count += pStep;
        }
        Assert.assertTrue(count >= pEnd);
    }
}

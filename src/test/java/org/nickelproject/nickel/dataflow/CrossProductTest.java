package org.nickelproject.nickel.dataflow;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.suites.UnitAnnotation;
import org.nickelproject.util.sources.Sequences;

import com.google.common.collect.Lists;

@UnitAnnotation
public class CrossProductTest {

    @Test
    public final void test() {
        final int size = 5;
        final List<Source<? extends Object>> sources = Lists.newArrayList();
        for (int i = 0; i < size; i++) {
            sources.add(Sequences.integer(0, size));
        }
        final Source<List<Object>> crossProduct = CrossProduct.crossProduct(sources);
        final List<List<Object>> list = Lists.newArrayList(crossProduct);
        for (final Object integer : list.get(0)) {
            Assert.assertEquals(integer, Integer.valueOf(0));
        }
        for (final Object integer : list.get(list.size() - 1)) {
            Assert.assertEquals(integer, Integer.valueOf(size - 1));
        }
        Assert.assertEquals((int) Math.pow(size, size), list.size());
    }
}

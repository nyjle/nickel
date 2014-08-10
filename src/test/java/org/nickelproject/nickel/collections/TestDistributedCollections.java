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
package org.nickelproject.nickel.collections;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nickelproject.nickel.TestModule;
import org.nickelproject.nickel.dataflow.Reductor;
import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.sources.SourceTestUtil;
import org.nickelproject.util.testUtil.UnitAnnotation;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;

@UnitAnnotation
public final class TestDistributedCollections {
    private static final int testSize = 1000;
    private static final int concatSize = 10;
    
    @BeforeClass
    public static void setup() {
        Guice.createInjector(new TestModule());
    }
    
    @Test
    public void testSink() {
        final int partSize = 5;
        final Reductor<Integer, DistributedCollection<Integer>> reductor = 
                new DistributedCollectionSink<Integer>(partSize).reductor();
        for (int i = 0; i < testSize; i++) {
            reductor.collect(i);
        }
        final DistributedCollection<Integer> collection = reductor.reduce();
        int i = 0;
        for (final Integer integer : SourceTestUtil.toIterable(collection)) {
            Assert.assertEquals(i++, integer.intValue());
        }
        Assert.assertEquals(testSize, i);
    }
    
    @Test
    public void testCreation() {
        final DistributedCollection<Double> collection = create(0, testSize);
        int counter = 0;
        for (final Double value : SourceTestUtil.toIterable(collection)) {
            counter++;
        }
        Assert.assertEquals(testSize, counter);
    }
    
    @Test
    public void testConcat() {
        final DistributedCollection<Double>[] array = createMany(testSize, concatSize);
        final DistributedCollection<Double> concat = DistributedCollectionUtil.concat(array);
        int counter = 0;
        for (final Double value : SourceTestUtil.toIterable(concat)) {
            counter++;
        }
        Assert.assertEquals(testSize * concatSize, counter);        
    }
    
    @Test
    public void testPartition() {
        final DistributedCollection<Double>[] array = createMany(testSize, concatSize);
        final DistributedCollection<Double> concat = DistributedCollectionUtil.concat(array);
        final Source<? extends Source<Double>> parts = concat.partition(concatSize);
        int counter = 0;
        for (final Source<Double> source : SourceTestUtil.toIterable(parts)) {
            counter++;
        }
        Assert.assertEquals(concatSize, counter);
    }
    
    private DistributedCollection<Double>[] createMany(final int size, final int count) {
        @SuppressWarnings("unchecked")
        final DistributedCollection<Double>[] array = new DistributedCollection[concatSize];
        for (int i = 0; i < count; i++) {
            array[i] = create(i * size, (i + 1) * size);
        }
        return array;
    }
    
    private DistributedCollection<Double> create(final int first, final int last) {
        Preconditions.checkArgument(last > first);
        final Double[] array = new Double[last - first];
        for (int i = first; i < last; i++) {
            array[i - first] = Double.valueOf(i);
        }
        return DistributedCollectionUtil.from(array);
    }
}

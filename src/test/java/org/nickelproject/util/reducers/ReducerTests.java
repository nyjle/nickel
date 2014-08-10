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
package org.nickelproject.util.reducers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.nickel.dataflow.Reducer;
import org.nickelproject.nickel.dataflow.Reductor;
import org.nickelproject.nickel.sources.SourceTestUtil;
import org.nickelproject.util.sources.Sequences;
import org.nickelproject.util.testUtil.UnitAnnotation;

import com.google.common.collect.Lists;

@UnitAnnotation
public final class ReducerTests {
    private static final int min = -200;
    private static final int max = 200;
    
    @Test
    public void integerSumReducerTest1() {
        int sum = min;
        final Reductor<Integer, Integer> integerSumReductor = new IntegerSumReducer(min).reductor();
        for (final Integer datum : SourceTestUtil.toIterable(Sequences.integer(0, max))) {
            integerSumReductor.collect(datum);
            sum += datum.intValue();
        }
        Assert.assertTrue(sum == integerSumReductor.reduce());
    }
    
    @Test
    public void integerSumReducerTest2() {
        int sum = 0;
        final Reductor<Integer, Integer> integerSumReductor = new IntegerSumReducer().reductor();
        for (final Integer datum : SourceTestUtil.toIterable(Sequences.integer(0, max))) {
            integerSumReductor.collect(datum);
            sum += datum.intValue();
        }
        Assert.assertTrue(sum == integerSumReductor.reduce());
    }
    
    @Test
    public void toListReducerTest() {
        final Reductor<List<Integer>, List<Integer>> toListReductor = new MergeListReducer<Integer>().reductor();
        for (final Integer datum : SourceTestUtil.toIterable(Sequences.integer(0, max))) {
            toListReductor.collect(Collections.singletonList(datum));
        }
        final List<Integer> list = toListReductor.reduce();
        final List<Integer> sequenceList = Lists.newArrayList(
                SourceTestUtil.toIterable(Sequences.integer(0, max)));
        Assert.assertEquals(sequenceList, list);
    }
    
    @Test
    public void countReducerTest() {
        final Reductor<Object, Integer> countReductor = ReducerUtil.count().reductor();
        for (final Integer datum : SourceTestUtil.toIterable(Sequences.integer(0, max))) {
            countReductor.collect(datum);
        }
        Assert.assertTrue(max == countReductor.reduce());
    }
    
    @Test
    public void averageReducerTest() {
        final Reductor<Double, Double> averageReductor = ReducerUtil.average().reductor();
        double sum = 0;
        for (double i = 0; i < max; i++) {
            averageReductor.collect(i);
            sum += i;
        }
        Assert.assertEquals(sum / max, averageReductor.reduce(), Math.ulp(0.0));
    }
    
    @Test
    public void maxReducerTest() {
        final List<Integer> data = Lists.newArrayList(
                SourceTestUtil.toIterable(Sequences.integer(min, max)));
        Collections.shuffle(data);
        final Reductor<Integer, Integer> maxReductor = new MaxReducer<Integer>().reductor();
        for (final Integer datum : data) {
            maxReductor.collect(datum);
        }
        Assert.assertTrue(max - 1 == maxReductor.reduce());
    }

    @Test
    public void minReducerTest() {
        final List<Integer> data = Lists.newArrayList(SourceTestUtil.toIterable(Sequences.integer(min, max)));
        Collections.shuffle(data);
        final Reductor<Integer, Integer> minReductor = new MinReducer<Integer>().reductor();
        for (final Integer datum : data) {
            minReductor.collect(datum);
        }
        Assert.assertTrue(min == minReductor.reduce());
    }
    
    @Test
    public void mapMergingReducerTest() {
        final Reducer<Map<Integer, Integer>> mapReducer =
                MapMergingReducer.create(new MaxReducer<Integer>());
        final Reductor<Map<Integer, Integer>, Map<Integer, Integer>> reductor = mapReducer.reductor();
        final int mapSize = 10;
        final List<Map<Integer, Integer>> list = Lists.newArrayList();
        for (int i = 1; i < mapSize; i++) {
            for (final Integer datum : SourceTestUtil.toIterable(Sequences.integer(min * i, max * i))) {
                list.add(Collections.singletonMap(i, datum));
            }
        }
        Collections.shuffle(list);
        for (final Map<Integer, Integer> map : list) {
            reductor.collect(map);
        }
        final Map<Integer, Integer> result = reductor.reduce();
        for (int i = 1; i < mapSize; i++) {
            Assert.assertTrue(max * i - 1 == result.get(i));
        }
    }
}

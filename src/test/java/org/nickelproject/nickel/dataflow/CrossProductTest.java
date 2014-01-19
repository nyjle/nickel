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

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.util.sources.Sequences;
import org.nickelproject.util.testUtil.UnitAnnotation;

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

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
package org.nickelproject.nickel.sources;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.util.sources.FileLineSource;
import org.nickelproject.util.streamUtil.ByteArrayInputStreamFactory;
import org.nickelproject.util.testUtil.UnitAnnotation;

import com.google.common.collect.Lists;

@UnitAnnotation
public final class SourceTests {
    private static final int size = 10;
    private byte[] bytes;
    
    @Before
    public void initialize() throws UnsupportedEncodingException {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < size; i++) {
            stringBuffer = stringBuffer.append(i).append("\n");
        }
        bytes = stringBuffer.toString().getBytes("UTF-8");
    }
    
    @Test
    public void testFileLineSource() {
        final Source<String> source = new FileLineSource(new ByteArrayInputStreamFactory(bytes));
        final List<String> list = Lists.newArrayList(SourceTestUtil.toIterable(source));
        for (int i = 0; i < size; i++) {
           final String line = new StringBuffer().append(i).toString();
           Assert.assertEquals(line, list.get(i));
        }
        Assert.assertEquals(size, list.size());
    }
}

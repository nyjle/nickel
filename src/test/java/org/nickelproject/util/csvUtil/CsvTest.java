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
package org.nickelproject.util.csvUtil;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.nickel.types.DoubleDataType;
import org.nickelproject.nickel.types.IntegerDataType;
import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.nickel.types.RecordDataType.Builder;
import org.nickelproject.nickel.types.StringDataType;
import org.nickelproject.suites.UnitAnnotation;

@UnitAnnotation
public final class CsvTest {

    @Test
    public void testParseHeader() {
        final String[] header = {"col1:String", "col2:Int", "col3:Double"};
        final RecordDataType schema = Header.parseHeader(header);
        Assert.assertEquals(0, schema.getIndex("col1"));
        Assert.assertEquals(1, schema.getIndex("col2"));
        Assert.assertEquals(2, schema.getIndex("col3"));
        Assert.assertEquals(StringDataType.class, schema.getType(0).getClass());
        Assert.assertEquals(IntegerDataType.class, schema.getType(1).getClass());
        Assert.assertEquals(DoubleDataType.class, schema.getType(2).getClass());
    }

    @Test
    public void testCreateHeader() {
        final Builder builder = new RecordDataType.Builder();
        builder.add("col1", new StringDataType());
        builder.add("col2", new IntegerDataType());
        builder.add("col3", new DoubleDataType());
        final RecordDataType schema = builder.build();
        final List<String> header = Header.createHeader(schema);
        Assert.assertEquals("col1:String", header.get(0));
        Assert.assertEquals("col2:Int", header.get(1));
        Assert.assertEquals("col3:Double", header.get(2));
    }

    @Test
    public void testIterator() {
        final double decimalNumber = 1.1;
        final int size = 3;
        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("col1:String,col2:Int,col3:Double\n");
        for (int i = 0; i < size; i++) {
            stringBuffer.append(i + "Number")
                        .append(",")
                        .append(i)
                        .append(",")
                        .append(i * decimalNumber)
                        .append("\n");
        }
        final Iterator<Record> iterator = new CsvIterator(new StringReader(stringBuffer.toString()));
        int count = 0;
        while (iterator.hasNext()) {
            final Record record = iterator.next();
            Assert.assertEquals(count + "Number", record.getField(0));
            Assert.assertEquals(count, record.getField(1));
            Assert.assertEquals(count * decimalNumber, record.getField(2));
            count++;
        }
    }
}

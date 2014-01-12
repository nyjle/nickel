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
import org.nickelproject.nickel.types.StringDataType;
import org.nickelproject.suites.UnitAnnotation;

@UnitAnnotation
public final class CsvTest {
    private static final double decimalNumber = 1.1;
    private static final int size = 3;

    @Test
    public void testReadSchema() {
        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("col1:String,col2:Int,col3:Double\n");
        checkSchema(CsvIterator.readSchema(new StringReader(stringBuffer.toString())));
    }
    
    @Test
    public void testParseHeader() {
        final String[] header = {"col1:String", "col2:Int", "col3:Double"};
        checkSchema(Header.parseHeader(header));
    }

    @Test
    public void testCreateHeader() {
        final RecordDataType schema = createSchema();
        final List<String> header = Header.createHeader(schema);
        Assert.assertEquals("col1:String", header.get(0));
        Assert.assertEquals("col2:Int", header.get(1));
        Assert.assertEquals("col3:Double", header.get(2));
    }

    @Test
    public void testIterator1() {
        final RecordDataType schema = createSchema();
        final StringBuffer stringBuffer = appendData(new StringBuffer());
        testIteratorData(new CsvIterator(new StringReader(stringBuffer.toString()), schema));
    }
    
    @Test
    public void testIterator2() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("col1:String,col2:Int,col3:Double\n");
        stringBuffer = appendData(stringBuffer);
        testIteratorData(new CsvIterator(new StringReader(stringBuffer.toString())));
    }
    
    private void checkSchema(final RecordDataType schema) {
        Assert.assertEquals(0, schema.getIndex("col1"));
        Assert.assertEquals(1, schema.getIndex("col2"));
        Assert.assertEquals(2, schema.getIndex("col3"));
        Assert.assertEquals(StringDataType.class, schema.getType(0).getClass());
        Assert.assertEquals(IntegerDataType.class, schema.getType(1).getClass());
        Assert.assertEquals(DoubleDataType.class, schema.getType(2).getClass());        
    }
    
    private RecordDataType createSchema() {
        return new RecordDataType.Builder()
                            .add("col1", new StringDataType())
                            .add("col2", new IntegerDataType())
                            .add("col3", new DoubleDataType())
                            .build();        
    }    

    private void testIteratorData(final Iterator<Record> iterator) {
        int count = 0;
        while (iterator.hasNext()) {
            final Record record = iterator.next();
            Assert.assertEquals(count + "Number", record.getField(0));
            Assert.assertEquals(count, record.getField(1));
            Assert.assertEquals(count * decimalNumber, record.getField(2));
            count++;
        }        
    }
    
    private StringBuffer appendData(final StringBuffer stringBuffer) {
        for (int i = 0; i < size; i++) {
            stringBuffer.append(i + "Number")
                        .append(",")
                        .append(i)
                        .append(",")
                        .append(i * decimalNumber)
                        .append("\n");
        }
        return stringBuffer;
    }
}

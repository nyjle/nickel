package org.nickelproject.lib.csvUtil;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.lib.types.IntegerDataType;
import org.nickelproject.lib.types.Record;
import org.nickelproject.lib.types.RecordDataType;
import org.nickelproject.lib.types.RecordDataType.Builder;
import org.nickelproject.lib.types.StringDataType;

public final class CsvTest {

    @Test
    public void testParseHeader() {
        final String[] header = {"col1:String", "col2:Int"};
        final RecordDataType schema = Header.parseHeader(header);
        Assert.assertEquals(0, schema.getIndex("col1"));
        Assert.assertEquals(1, schema.getIndex("col2"));
        Assert.assertEquals(StringDataType.class, schema.getType(0).getClass());
        Assert.assertEquals(IntegerDataType.class, schema.getType(1).getClass());
    }

    @Test
    public void testCreateHeader() {
        final Builder builder = new RecordDataType.Builder();
        builder.add("col1", new StringDataType());
        builder.add("col2", new IntegerDataType());
        final RecordDataType schema = builder.build();
        final List<String> header = Header.createHeader(schema);
        Assert.assertEquals("col1:String", header.get(0));
        Assert.assertEquals("col2:Int", header.get(1));
    }

    @Test
    public void testIterator() {
        final String data = "col1:String,col2:Int\n" + "one, 1\n" + "two, 2\n" + "three, 3\n";
        final Iterator<Record> iterator = new CsvIterator(new StringReader(data));
        final Record record1 = iterator.next();
        Assert.assertEquals("one", record1.getField(0));
        Assert.assertEquals(1, record1.getField(1));
        final Record record2 = iterator.next();
        Assert.assertEquals("two", record2.getField(0));
        Assert.assertEquals(2, record2.getField(1));
        final int three = 3;
        final Record record3 = iterator.next();
        Assert.assertEquals("three", record3.getField(0));
        Assert.assertEquals(three, record3.getField(1));
    }
}

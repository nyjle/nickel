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

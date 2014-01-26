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
        final List<String> list = Lists.newArrayList(source);
        for (int i = 0; i < size; i++) {
           final String line = new StringBuffer().append(i).toString();
           Assert.assertEquals(line, list.get(i));
        }
        Assert.assertEquals(size, list.size());
    }
}

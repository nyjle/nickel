package org.nickelproject.util.functions;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nickelproject.nickel.TestModule;
import org.nickelproject.nickel.externalReference.ExternalReference;
import org.nickelproject.suites.UnitAnnotation;

import com.google.common.base.Function;
import com.google.inject.Guice;

@UnitAnnotation
public final class ExternalFunctionTests {
    private static final String testString = "This is a test string";
    
    @Before
    public void initialize() {
        Guice.createInjector(new TestModule());
    }
    
    @Test
    public void testGetExternal() {
        final ExternalReference<String> stringReference = ExternalReference.of(testString);
        final String resultString = new GetExternal<String>().apply(stringReference);
        Assert.assertEquals(testString, resultString);
    }

    @Test
    public void testPutExternal() {
        final ExternalReference<String> stringReference = new PutExternal<String>().apply(testString);
        Assert.assertEquals(testString, stringReference.get());
    }
    
    @Test
    public void testExternalize() {
        final int end = 5;
        final Function<String, String> function = new SubString(0, end);
        final Function<ExternalReference<String>, ExternalReference<String>> externalFunction =
                FunctionUtil.externalize(function);
        final ExternalReference<String> stringReference = ExternalReference.of(testString);
        Assert.assertEquals(testString.substring(0, end), externalFunction.apply(stringReference).get());
    }
}

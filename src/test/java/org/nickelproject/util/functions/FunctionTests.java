package org.nickelproject.util.functions;


import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.nickel.dataflow.ToMapFunction;
import org.nickelproject.util.testUtil.UnitAnnotation;
import org.nickelproject.util.tuple.Pair;
import org.nickelproject.util.tuple.Quadruple;

import com.google.common.base.Function;

@UnitAnnotation
public final class FunctionTests {

    @Test
    public void testToMap() {
        final Function<Pair<Integer, String>, Map<Integer, String>> function =
                new ToMapFunction<Integer, String>();
        final Pair<Integer, String> pair = Pair.of(1, "one");
        final Map<Integer, String> map = function.apply(pair);
        Assert.assertEquals("one", map.get(1));
    }
        
    @Test
    public void testSubString() {
        final Function<String, String> function = new SubString(0, 5);
        Assert.assertEquals("01234", function.apply("0123456789"));
        Assert.assertEquals("0123", function.apply("0123"));
        Assert.assertEquals("", function.apply(""));
    }
    
    @Test
    public void testPairFunction() {
        final Function<String, String> firstFunction = new SubString(0, 2);
        final Function<String, String> secondFunction = new SubString(2, 4);
        final Function<Pair<String, String>, Pair<String, String>> pairFunction =
                PairFunction.of(firstFunction, secondFunction);
        final Pair<String, String> pair = pairFunction.apply(Pair.of("01234", "1234"));
        Assert.assertEquals("01", pair.getA());
        Assert.assertEquals("23", pair.getB());
    }
    
    @Test
    public void testArrayElementFunction() {
        final int index = 5;
        final Integer[] array = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
        final Function<Integer[], Integer> arrayElementFunction = new ArrayElementFunction<Integer>(index);
        Assert.assertTrue(array[index] == arrayElementFunction.apply(array));
    }
    
    @Test
    public void testProjectFunction() {
        final int[] indices = new int[]{2, 5, 6};
        final Integer[] array = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
        final Function<Integer[], Integer[]> projectFunction = new ProjectFunction<Integer>(indices);
        final Integer[] result = projectFunction.apply(array);
        for (int i = 0; i < indices.length; i++) {
            Assert.assertTrue(indices[i] == result[i]);
        }
    }
    
    @Test
    public void testQuadrupleFunction() {
        final Function<Integer[], Integer> function0 = new ArrayElementFunction<Integer>(0);
        final Function<Integer[], Integer> function1 = new ArrayElementFunction<Integer>(1);
        final Function<Integer[], Integer> function2 = new ArrayElementFunction<Integer>(2);
        final Function<Integer[], Integer> function3 = new ArrayElementFunction<Integer>(3);
        final Integer[] array = new Integer[]{0, 1, 2, 3};
        final Function<Integer[], Quadruple<Integer, Integer, Integer, Integer>> quadrupleFunction =
                QuadrupleFunction.of(function0, function1, function2, function3);
        final Quadruple<Integer, Integer, Integer, Integer> quadruple = quadrupleFunction.apply(array);
        Assert.assertEquals(array[0], quadruple.getA());
        Assert.assertEquals(array[1], quadruple.getB());
        Assert.assertEquals(array[2], quadruple.getC());
        //CHECKSTYLE:OFF
        Assert.assertEquals(array[3], quadruple.getD());
        //CHECKSTYLE:ON
    }
}

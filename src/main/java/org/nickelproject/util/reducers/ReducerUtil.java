package org.nickelproject.util.reducers;

import org.nickelproject.nickel.dataflow.Reducer;
import org.nickelproject.nickel.dataflow.Reducers;

import com.google.common.base.Functions;

public final class ReducerUtil {
    private static Reducer<Object, Integer> count() {
        return Reducers.compose(Functions.constant(1), new IntegerSumReducer(), Functions.<Integer>identity());
    }
}

package org.nickelproject.util.reducers;

import org.nickelproject.nickel.dataflow.Reducer;
import org.nickelproject.nickel.dataflow.Reductor;

/**
 * @author Nigel
 */
public final class MaxReducer<T extends Comparable<T>> implements Reducer<T, T> {
    private static final long serialVersionUID = 1L;

    @Override
    public Reductor<T, T> reductor() {
        return new MaxReductor();
    }

    private class MaxReductor implements Reductor<T, T> {
        private T mMax = null;

        @Override
        public void collect(final T pVal) {
            mMax = mMax == null ? pVal : pVal.compareTo(mMax) > 0 ? pVal : mMax;
        }

        @Override
        public T reduce() {
            return mMax;
        }
    }
}

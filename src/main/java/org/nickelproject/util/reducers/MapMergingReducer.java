package org.nickelproject.util.reducers;

import java.util.Map;

import org.nickelproject.nickel.dataflow.Reducer;
import org.nickelproject.nickel.dataflow.Reductor;

import com.google.common.collect.Maps;

public class MapMergingReducer<T, S, U> implements Reducer<Map<T, S>, Map<T, U>> {
    private static final long   serialVersionUID = 1L;
    private final Reducer<S, U> reducer;

    private MapMergingReducer(final Reducer<S, U> reducer) {
        this.reducer = reducer;
    }

    public static <T, S, U> Reducer<Map<T, S>, Map<T, U>> create(final Reducer<S, U> reducer) {
        return new MapMergingReducer<T, S, U>(reducer);
    }

    @Override
    public Reductor<Map<T, S>, Map<T, U>> reductor() {
        return new MapReductor();
    }

    private class MapReductor implements Reductor<Map<T, S>, Map<T, U>> {
        private final Map<T, Reductor<S, U>> reductorMap = Maps.newHashMap();

        @Override
        public void collect(final Map<T, S> pVal) {
            for (final T key : pVal.keySet()) {
                Reductor<S, U> reductor = reductorMap.get(key);
                if (reductor == null) {
                    reductor = reducer.reductor();
                    reductorMap.put(key, reductor);
                }
                reductor.collect(pVal.get(key));
            }
        }

        @Override
        public Map<T, U> reduce() {
            final Map<T, U> retVal = Maps.newHashMap();
            for (final T key : reductorMap.keySet()) {
                retVal.put(key, reductorMap.get(key).reduce());
            }
            return retVal;
        }
    }
}

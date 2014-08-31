package org.nickelproject.nickel.dataflow;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;

public final class MapToPairSourceFunction<K, V> implements Function<Map<K, V>, Source<Pair<K, V>>> {

    @Override
    public Source<Pair<K, V>> apply(@Nonnull final Map<K, V> input) {
        return Sources.transform(
                Sources.from(input.entrySet()), 
                    new Function<Map.Entry<K, V>, Pair<K, V>>() {
    
                        @Override
                        public Pair<K, V> apply(@Nonnull final Entry<K, V> input) {
                            return Pair.of(input.getKey(), input.getValue());
                        }                    
                    }
               );
    }
}

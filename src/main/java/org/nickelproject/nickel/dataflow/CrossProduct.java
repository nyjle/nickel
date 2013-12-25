package org.nickelproject.nickel.dataflow;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class CrossProduct {
    
    public static Source<List<Object>> crossProduct(final List<Source<? extends Object>> sources) {
        final Source<? extends Object> firstSource = sources.get(0);
        Source<List<Object>> retVal = Sources.transform(firstSource, new Function<Object, List<Object>>() {
            @Override
            public List<Object> apply(final Object input) {
                return Lists.newArrayList(input);
            }                    
        });
        for (int i = 1; i < sources.size(); i++) {
            retVal = Sources.concat(Sources.transform(sources.get(i), new AddToList(retVal)));
        }
        return retVal;
    }
    
    private static final class AddToList implements Function<Object, Source<List<Object>>> {
        private final Source<List<Object>> source;

        public AddToList(final Source<List<Object>> source) {
            this.source = source;
        }
        
        @Override
        public Source<List<Object>> apply(final Object input) {
            return Sources.transform(source, new Function<List<Object>, List<Object>>() {
                @Override
                public List<Object> apply(final List<Object> list) {
                    list.add(input);
                    return list;
                }
            });
        }        
    }
}

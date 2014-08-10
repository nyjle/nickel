package org.nickelproject.nickel.mapReduce;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.util.CloseableIterator;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

/**
 * This is temporary, we have to figure out how to do this right moving forward.
 */
@Beta
public final class MapperUtil {
    
    private MapperUtil() {
        // Prevents Construction
    }
    
    public static <S, T> Source<S> mapSource(final Source<T> source, final Function<T, S> transform,
            final Mapper map) {
        return new MappedSource<S, T>(source, transform, map);
    }
    
    private static final class MappedSource<S, T> implements Source<S> {
        private final Source<T> originalSource;
        private final Function<T, S> transform;
        private final Mapper mapper;
        
        private MappedSource(final Source<T> originalSource, final Function<T, S> transform, 
                final Mapper mapper) {
            this.originalSource = originalSource;
            this.transform = transform;
            this.mapper = mapper;
        }
        
        @Override
        public CloseableIterator<S> iterator() {
            return mapper.map(originalSource.iterator(), transform);
        }
        
        @Override
        public Source<? extends Source<S>> partition(final int sizeGuideline) {
            final Function<Source<T>, Source<S>> sourceTransform = new Function<Source<T>, Source<S>>() {
                @Override
                public Source<S> apply(final Source<T> input) {
                    return mapSource(input, transform, mapper);
                }
            };
            final Source<Source<T>> wrappedPartitions = 
                    (Source<Source<T>>) originalSource.partition(sizeGuideline);
            return Sources.transform(wrappedPartitions, sourceTransform);
        }
        
        @Override
        public int size() {
            return Source.unknownSize;
        }
    }
}

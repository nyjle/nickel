package org.nickelproject.nickel.mapReduce;

import java.util.Iterator;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

/**
 * This is temporary, we have to figure out how to do this right moving forward
 */
@Beta
public class MapperUtil {
    
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
        public Iterator<S> iterator() {
            return mapper.map(originalSource.iterator(), transform);
        }
        
        @Override
        public Source<? extends Source<S>> partition(int sizeGuideline) {
            return Sources.singleton(this);
        }
        
        @Override
        public int size() {
            return Source.unknownSize;
        }
    }
}

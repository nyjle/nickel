package org.nickelproject.nickel.dataflow;

import java.util.Arrays;
import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public final class Sources {

    private Sources() {
        // Prevents construction
    }
    
    public static <S> Source<S> limit(final Source<S> pSource, final int pLimit) {
        return from(Iterables.limit(pSource, pLimit));
    }

    public static <T> Source<T> concat(final Source<T>... sources) {
        return concat(from(sources));
    }
    
    public static <T> Source<T> concat(final Source<Source<T>> sources) {
        return new ConcatSource<T>(sources);
    }
    
    public static <T> Source<T> filter(final Source<T> source, final Predicate<T> predicate) {
        return new FilteredSource<T>(source, predicate);
    }

    public static <S, T> Source<T> transform(final Source<S> source, final Function<S, T> function) {
        return new TransformedSource<S, T>(source, function);
    }

    public static <S> Source<S> from(final Iterable<S> iterable) {
        return new IterableSource<S>(iterable);
    }

    public static <S> Source<S> from(final S[] array) {
        return new IterableSource<S>(Arrays.asList(array));
    }
    
    private static class ConcatSource<S> implements Source<S> {
        private static final long serialVersionUID = 1L;
        private final Source<Source<S>> sources;
        
        public ConcatSource(final Source<Source<S>> sources) {
            this.sources = sources;
        }

        @Override
        public Iterator<S> iterator() {
            return Iterators.concat(
                    Iterators.transform(sources.iterator(), 
                            new Function<Source<S>, Iterator<S>>() {
                                @Override
                                public Iterator<S> apply(final Source<S> input) {
                                    return input.iterator();
                                }
                            }));
        }

        @Override
        public Source<Source<S>> partition(final int partitionSize) {
            return sources;
        }
    }
    
    private static class IterableSource<S> implements Source<S> {
        private static final long serialVersionUID = 1L;
        private final Iterable<S> iterable;

        public IterableSource(final Iterable<S> iterable) {
            this.iterable = iterable;
        }

        @Override
        public Iterator<S> iterator() {
            return iterable.iterator();
        }

        @Override
        public Source<Source<S>> partition(final int partitionSize) {
            return from(Iterables.transform(Iterables.partition(iterable, partitionSize),
                    new Function<Iterable<S>, Source<S>>() {
                        @Override
                        public Source<S> apply(final Iterable<S> partition) {
                            return from(partition);
                        }
                    }));
        }
    }

    private static class TransformedSource<S, T> implements Source<T> {
        private static final long    serialVersionUID = 1L;
        private final Source<S>      wrappedSource;
        private final Function<S, T> transform;

        public TransformedSource(final Source<S> wrappedSource, final Function<S, T> transform) {
            this.wrappedSource = wrappedSource;
            this.transform = transform;
        }

        @Override
        public Iterator<T> iterator() {
            return Iterators.transform(wrappedSource.iterator(), transform);
        }

        @Override
        public Source<Source<T>> partition(final int partitionSize) {
            return transform(wrappedSource.partition(partitionSize), 
                    new Function<Source<S>, Source<T>>() {
                        @Override
                        public Source<T> apply(final Source<S> input) {
                            return transform(input, transform);
                        }
                    });
        }
    }

    private static class FilteredSource<T> implements Source<T> {
        private static final long  serialVersionUID = 1L;
        private final Source<T>    wrappedSource;
        private final Predicate<T> predicate;

        public FilteredSource(final Source<T> wrappedSource, final Predicate<T> predicate) {
            this.wrappedSource = wrappedSource;
            this.predicate = predicate;
        }

        @Override
        public Iterator<T> iterator() {
            return Iterators.filter(wrappedSource.iterator(), predicate);
        }

        @Override
        public Source<Source<T>> partition(final int partitionSize) {
            return transform(wrappedSource.partition(partitionSize),
                    new Function<Source<T>, Source<T>>() {
                        @Override
                        public Source<T> apply(final Source<T> input) {
                            return filter(input, predicate);
                        }
                    });
        }
    }
}

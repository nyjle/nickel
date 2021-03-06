/*
 * Copyright (c) 2013 Nigel Duffy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nickelproject.nickel.dataflow;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.nickelproject.util.CloseableIterator;
import org.nickelproject.util.TrivialCloseableIterator;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public final class Sources {

    private Sources() {
        // Prevents construction
    }
    
    public static <S> Source<S> limit(final Source<S> pSource, final int pLimit) {
        return new Source<S>() {
            private static final long serialVersionUID = 1L;

            @Override
            public CloseableIterator<S> iterator() {
                return TrivialCloseableIterator.create(
                        Iterators.limit(pSource.iterator(), pLimit));
            }

            @Override
            public Source<? extends Source<S>> partition(final int sizeGuideline) {
                return Sources.singleton(this);
            }

            @Override
            public int size() {
                return pLimit;
            }           
        };
    }

    public static <S> Source<S> singleton(final S object) {
        return from(Collections.singleton(object));
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

    public static <S, T> Source<T> transform(final Source<? extends S> source, final Function<S, T> function) {
        return new TransformedSource<S, T>(source, function);
    }

    public static <S> Source<S> from(final Iterable<S> iterable) {
        return new IterableSource<S>(iterable);
    }

    public static <S> Source<S> from(final S[] array) {
        return from(Arrays.asList(array));
    }
    
    public static <S> Source<S> from(final List<S> list) {
        return new IterableSource<S>(list, list.size());
    }
    
    private static class ConcatSource<S> implements Source<S> {
        private static final long serialVersionUID = 1L;
        private final Source<Source<S>> sources;
        
        public ConcatSource(final Source<Source<S>> sources) {
            this.sources = sources;
        }

        @Override
        public int size() {
            return Source.unknownSize;
        }
        
        @Override
        public CloseableIterator<S> iterator() {
            return TrivialCloseableIterator.create(
                    Iterators.concat(
                            Iterators.transform(sources.iterator(), 
                                    new Function<Source<S>, Iterator<S>>() {
                                        @Override
                                        public Iterator<S> apply(final Source<S> input) {
                                            return input.iterator();
                                        }
                                })));
        }

        @Override
        public Source<Source<S>> partition(final int partitionSize) {
            return sources;
        }
    }
    
    private static class IterableSource<S> implements Source<S> {
        private static final long serialVersionUID = 1L;
        private final Iterable<S> iterable;
        private final int size;

        public IterableSource(final Iterable<S> iterable, final int size) {
            this.iterable = iterable;
            this.size = size;
        }

        public IterableSource(final Iterable<S> iterable) {
            this(iterable, Source.unknownSize);
        }
        
        @Override
        public int size() {
            return size;
        }
        
        @Override
        public CloseableIterator<S> iterator() {
            return TrivialCloseableIterator.create(iterable.iterator());
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
        private final Source<? extends S>      wrappedSource;
        private final Function<S, T> transform;

        public TransformedSource(final Source<? extends S> wrappedSource, final Function<S, T> transform) {
            this.wrappedSource = wrappedSource;
            this.transform = transform;
        }

        @Override
        public int size() {
            return wrappedSource.size();
        }
        
        @Override
        public CloseableIterator<T> iterator() {
            return TrivialCloseableIterator.create(
                    Iterators.transform(wrappedSource.iterator(), transform));
        }

        @Override
        public Source<Source<T>> partition(final int partitionSize) {
            final Function<Source<S>, Source<T>> sourceTransform = new Function<Source<S>, Source<T>>() {
                    @Override
                    public Source<T> apply(final Source<S> input) {
                        return transform(input, transform);
                    }
                };
            final Source<Source<S>> wrappedPartitions = 
                    (Source<Source<S>>) wrappedSource.partition(partitionSize);
            return transform(wrappedPartitions, sourceTransform);
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
        public int size() {
            return Source.unknownSize;
        }
        
        @Override
        public CloseableIterator<T> iterator() {
            return TrivialCloseableIterator.create(
                    Iterators.filter(wrappedSource.iterator(), predicate));
        }

        @Override
        public Source<Source<T>> partition(final int partitionSize) {
            final Source<Source<T>> wrappedPartition =
                    (Source<Source<T>>) wrappedSource.partition(partitionSize);
            return transform(wrappedPartition,
                    new Function<Source<T>, Source<T>>() {
                        @Override
                        public Source<T> apply(final Source<T> input) {
                            return filter(input, predicate);
                        }
                    });
        }
    }
}

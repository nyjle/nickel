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
package org.nickelproject.nickel.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.externalReference.ExternalReference;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

final class InnerNode<T> implements DistributedCollection<T> {
    private static final long serialVersionUID = 1L;
    //CHECKSTYLE:OFF 
    private final ExternalReference<DistributedCollection<T>[]> nodes;
    //CHECKSTYLE:ON 
    private final int size;
    
    public InnerNode(final DistributedCollection<T>[] nodes) {
        this.nodes = ExternalReference.of(nodes);
        this.size = nodes.length;
    }

    @SuppressWarnings("unchecked")
    public InnerNode(final List<DistributedCollection<T>> nodes) {
        this(nodes.toArray(new DistributedCollection[0]));
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public Source<Source<T>> partition(final int partitionSize) {
        final List<Source<T>> list = Lists.newArrayList();
        list.addAll(Arrays.asList(nodes.get()));
        return Sources.from(list);
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.concat(
                Iterators.transform(Arrays.asList(nodes.get()).iterator(), 
                    new Function<DistributedCollection<T>, Iterator<T>>() {
                        @Override
                        public Iterator<T> apply(final DistributedCollection<T> input) {
                            return input.iterator();
                        }
                    })
                );
    }
}

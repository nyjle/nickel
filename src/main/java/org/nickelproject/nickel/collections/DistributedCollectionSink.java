/*
 * Copyright (c) 2013, 2014 Nigel Duffy
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

import java.util.List;

import org.nickelproject.nickel.dataflow.Reductor;
import org.nickelproject.nickel.dataflow.Sink;

import com.google.common.collect.Lists;

public final class DistributedCollectionSink<T> implements Sink<T, DistributedCollection<T>> {
    private static final long serialVersionUID = 1L;
    private final int nodeSize;
    
    public DistributedCollectionSink(final int nodeSize) {
        this.nodeSize = nodeSize;
    }
    
    @Override
    public Reductor<T, DistributedCollection<T>> reductor() {
        return new DistributedCollectionReductor<T>(nodeSize);
    }

    private static final class DistributedCollectionReductor<T> 
                                            implements Reductor<T, DistributedCollection<T>> {
        private final List<T> data = Lists.newArrayList();
        private final List<DistributedCollection<T>> nodes = Lists.newArrayList();
        private final int nodeSize;
        
        public DistributedCollectionReductor(final int nodeSize) {
            this.nodeSize = nodeSize;
        }
        
        @Override
        public void collect(final T pVal) {
            data.add(pVal);
            if (data.size() > nodeSize) {
                nodes.add(DistributedCollectionUtil.from(data));
                data.clear();
            }
        }

        @Override
        public DistributedCollection<T> reduce() {
            if (data.size() > 0) {
                nodes.add(DistributedCollectionUtil.from(data));
            }
            return DistributedCollectionUtil.concat(nodes);
        }
    }
}

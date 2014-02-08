package org.nickelproject.nickel.collections;

import java.util.List;

import org.nickelproject.nickel.dataflow.Reductor;
import org.nickelproject.nickel.dataflow.Sink;

import com.google.common.collect.Lists;

public final class DistributedCollectionReducer<T> implements Sink<T, DistributedCollection<T>> {
    private static final long serialVersionUID = 1L;
    private final int nodeSize;
    
    public DistributedCollectionReducer(final int nodeSize) {
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

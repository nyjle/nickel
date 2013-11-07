package org.nickelproject.nickel.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.externalReference.ExternalReference;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

final class InnerNode<T> extends DistributedCollection<T> {
    private static final long serialVersionUID = 1L;
    private ExternalReference<DistributedCollection<T>[]> nodes; // Not final as serializable
    
    public InnerNode(final DistributedCollection<T>[] nodes) {
        this.nodes = ExternalReference.of(nodes);
    }

    @SuppressWarnings("unchecked")
    public InnerNode(final List<DistributedCollection<T>> nodes) {
        this(nodes.toArray(new DistributedCollection[0]));
    }
    
    @Override
    public DistributedCollection<T>[] getNodes() {
        return nodes.get();
    }
    
    @Override
    public Source<? extends Source<T>> partition(final int partitionSize) {
        return Sources.from(Arrays.asList(nodes.get()));
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
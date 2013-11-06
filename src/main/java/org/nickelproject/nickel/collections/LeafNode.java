package org.nickelproject.nickel.collections;

import java.util.Iterator;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.externalReference.ExternalReference;

import com.google.common.collect.Iterators;

final class LeafNode<T> extends DistributedCollection<T> {
    private static final long serialVersionUID = 1L;
    private ExternalReference<T[]> data; // Not final because its Serialized
    
    public LeafNode(final T[] data) {
        this.data = ExternalReference.of(data);
    }

    @Override
    public Source<Source<T>> partition(int partitionSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.forArray(data.get());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public DistributedCollection<T>[] getNodes() {
        return new DistributedCollection[]{this};
    }    
}
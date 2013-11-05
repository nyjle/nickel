package org.nickelproject.nickel.collections;

import java.io.Serializable;
import java.util.Iterator;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.externalReference.ExternalReference;

import com.google.common.base.Function;

public class DistributedList<T extends Serializable> implements Source<T> {
    
    @Override
    public Iterator<T> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Source<Source<T>> partition(int partitionSize) {
        // TODO Auto-generated method stub
        return null;
    }
    
    private static interface Node<T> extends Serializable {
        Source<T> getSource();
    }
    
    private static final class InnerNode<T> implements Node<T> {
        private static final long serialVersionUID = 1L;
        private final ExternalReference<Node<T>>[] nodes;
        
        public InnerNode(final ExternalReference<Node<T>>[] nodes) {
            this.nodes = nodes;
        }
        
        public Source<T> getSource() {
            return Sources.concat(
                    Sources.transform(
                            Sources.from(nodes),
                            new Function<ExternalReference<Node<T>>, Source<T>>() {
                                @Override
                                public Source<T> apply(final ExternalReference<Node<T>> input) {
                                    return input.get().getSource();
                                }
                            }));
        }
    }
    
    private static final class LeafNode<T> implements Node<T> {
        private static final long serialVersionUID = 1L;
        private T[] data; // Not final because its Serialized
        
        public LeafNode(final T[] data) {
            this.data = data;
        }
        
        public Source<T> getSource() {
            return Sources.from(data);
        }
    }
}

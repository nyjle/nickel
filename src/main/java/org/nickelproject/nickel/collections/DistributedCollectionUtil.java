package org.nickelproject.nickel.collections;


public final class DistributedCollectionUtil {

    private DistributedCollectionUtil() {
        // Prevents construction
    }
    
    public static <T> DistributedCollection<T> from(final T[] data) {
        return new LeafNode<T>(data);
    }
    
    // This needs to be careful about size
    public static <T> DistributedCollection<T> concat(final DistributedCollection<T>[] collections) {
        return new InnerNode<T>(collections);
    }
}

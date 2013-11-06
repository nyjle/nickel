package org.nickelproject.nickel.collections;

import java.util.List;

import com.google.common.collect.Lists;

public final class DistributedCollectionUtil {
    private static final int maxInnerNodeSize = 1000;

    private DistributedCollectionUtil() {
        // Prevents construction
    }
    
    public static <T> DistributedCollection<T> from(final T[] data) {
        return new LeafNode<T>(data);
    }
    
    // This needs to be careful about size
    public static <T> DistributedCollection<T> 
                concat(final DistributedCollection<? extends T>...collections) {
        final List<DistributedCollection<? extends T>> list = Lists.newArrayList();
        for (final DistributedCollection<? extends T> collection : collections) {
            for(final DistributedCollection<? extends T> part : collection.getNodes()) {
                list.add(part);
            }
        }
        return new InnerNode<T>(list);
    }
}

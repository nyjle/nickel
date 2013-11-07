package org.nickelproject.nickel.collections;

import org.nickelproject.nickel.dataflow.Source;

public abstract class DistributedCollection<T> implements Source<T> {
    private static final long serialVersionUID = 1L;

    abstract DistributedCollection<T>[] getNodes();
}

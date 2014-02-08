package org.nickelproject.nickel.dataflow;

public interface Partitionable<T> {
    Source<? extends Source<T>> partition(final int partitionSize);
}

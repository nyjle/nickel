package org.nickelproject.nickel.dataflow;

import java.io.Serializable;

public interface Source<T> extends Iterable<T>, Serializable {
    Source<Source<T>> partition(final int partitionSize);
}

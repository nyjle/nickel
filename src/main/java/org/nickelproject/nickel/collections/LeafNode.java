/*
 * Copyright (c) 2013 Nigel Duffy
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

import java.util.Collections;
import java.util.Iterator;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.externalReference.ExternalReference;

import com.google.common.collect.Iterators;

final class LeafNode<T> extends DistributedCollection<T> {
    private static final long serialVersionUID = 1L;
    private ExternalReference<T[]> data; // Not final because its Serialized
    
    public LeafNode(final T[] data) {
        this.data = ExternalReference.of(data);
    }

    @Override
    public Source<Source<T>> partition(final int partitionSize) {
        return Sources.from(Collections.<Source<T>>singletonList(this));
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

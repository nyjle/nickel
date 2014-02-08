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

import java.lang.reflect.Array;
import java.util.List;

import com.google.common.base.Preconditions;


public final class DistributedCollectionUtil {

    private DistributedCollectionUtil() {
        // Prevents construction
    }
    
    public static <T> DistributedCollection<T> from(final T[] data) {
        return new LeafNode<T>(data);
    }
    
    public static <T> DistributedCollection<T> from(final List<T> data) {
        Preconditions.checkArgument(data.size() > 0);
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(data.get(0).getClass(), data.size());
        return new LeafNode<T>(data.toArray(array));
    }
    
    // This needs to be careful about size
    public static <T> DistributedCollection<T> concat(final DistributedCollection<T>[] collections) {
        return new InnerNode<T>(collections);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> DistributedCollection<T> concat(final List<DistributedCollection<T>> collections) {
        Preconditions.checkArgument(collections.size() > 0);
        return new InnerNode<T>(collections.toArray(new DistributedCollection[0]));
    }
}

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
package org.nickelproject.nickel.objectStore;

import java.util.concurrent.ExecutionException;

import org.nickelproject.nickel.blobStore.BlobRef;
import org.nickelproject.nickel.blobStore.BlobStore;
import org.nickelproject.util.RethrownException;
import org.nickelproject.util.RetryProxy;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public final class CachingObjectStore implements ObjectStore {
    private final ObjectStore                   objectStore;
    private final LoadingCache<BlobRef, Object> cache = CacheBuilder.newBuilder().build(
            new CacheLoader<BlobRef, Object>() {
                @Override
                public Object load(final BlobRef key)
                        throws Exception {
                    return objectStore.get(key);
                }
            });

    public CachingObjectStore(final ObjectStore objectStore) {
        this.objectStore = RetryProxy.newInstance(ObjectStore.class, objectStore);
    }

    @Inject
    public CachingObjectStore(final BlobStore blobStore) {
        this(new WrappedBlobStore(blobStore));
    }

    @Override
    public boolean contains(final BlobRef blob) {
        return cache.getIfPresent(blob) != null || objectStore.contains(blob);
    }

    @Override
    public Object get(final BlobRef blob) {
        try {
            return contains(blob) ? cache.get(blob) : null;
        } catch (final ExecutionException e) {
            throw RethrownException.rethrow(e);
        }
    }

    @Override
    public BlobRef put(final Object data) {
        final BlobRef blobRef = objectStore.put(data);
        cache.put(blobRef, data);
        return blobRef;
    }
}

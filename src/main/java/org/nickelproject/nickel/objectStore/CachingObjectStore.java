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

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import org.nickelproject.nickel.blobStore.BlobRef;
import org.nickelproject.nickel.blobStore.BlobStore;
import org.nickelproject.util.RethrownException;
import org.nickelproject.util.RetryProxy;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public final class CachingObjectStore implements ObjectStore {
    private final BlobStore                   blobStore;
    private final LoadingCache<BlobRef, Object> cache;

    @Inject
    public CachingObjectStore(final BlobStore blobStore, final CacheBuilderSpec cacheBuilderSpec) {
        this.blobStore = RetryProxy.newInstance(BlobStore.class, blobStore);
        this.cache = CacheBuilder.from(cacheBuilderSpec)
                .build(
                    new CacheLoader<BlobRef, Object>() {
                        @Override
                        public Object load(final BlobRef key) throws Exception {
                            return Util.objectFromStream(blobStore.getAsStream(key));
                        }
                    });
    }

    @Override
    public boolean contains(final BlobRef blob) {
        return cache.getIfPresent(blob) != null || blobStore.contains(blob);
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
        final byte[] bytes = Util.bytesFromObject((Serializable) data);
        BlobRef blobRef = BlobRef.keyFromBytes(bytes);
        if (cache.getIfPresent(blobRef) == null) {
            blobRef = blobStore.put(bytes);
            cache.put(blobRef, data);
        }
        return blobRef;
    }
}

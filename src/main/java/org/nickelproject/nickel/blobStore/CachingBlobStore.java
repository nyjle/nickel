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
package org.nickelproject.nickel.blobStore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.nickelproject.util.RethrownException;
import org.nickelproject.util.RetryProxy;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public final class CachingBlobStore implements BlobStore {
    private final BlobStore                   blobStore;
    private final LoadingCache<BlobRef, byte[]> cache; 

    @Inject
    public CachingBlobStore(final BlobStore blobStore, final CacheBuilderSpec cacheBuilderSpec) {
        this.blobStore = RetryProxy.newInstance(BlobStore.class, blobStore);
        this.cache = CacheBuilder.from(cacheBuilderSpec)
                .weigher(new Weigher<BlobRef, byte[]>() {

                    @Override
                    public int weigh(BlobRef key, byte[] value) {
                        return value.length;
                    }
                })
                .build(
                    new CacheLoader<BlobRef, byte[]>() {
                        @Override
                        public byte[] load(final BlobRef key) throws Exception {
                            return blobStore.get(key);
                        }
                    });
    }

    @Override
    public boolean contains(final BlobRef blob) {
        return cache.getIfPresent(blob) != null || blobStore.contains(blob);
    }

    @Override
    public byte[] get(final BlobRef blob) {
        try {
            return contains(blob) ? cache.get(blob) : null;
        } catch (final ExecutionException e) {
            throw RethrownException.rethrow(e);
        }
    }

    @Override
    public BlobRef put(final byte[] data) {
        BlobRef blobRef = BlobRef.keyFromBytes(data);
        if (cache.getIfPresent(blobRef) == null) {
            blobRef = blobStore.put(data);
            cache.put(blobRef, data);
        }
        return blobRef;
    }

    @Override
    public InputStream getAsStream(final BlobRef blobRef) {
        final byte[] bytes = get(blobRef);
        return bytes == null ? null : new ByteArrayInputStream(bytes);
    }

    @Override
    public BlobRef put(final InputStream stream) {
        Preconditions.checkNotNull(stream);
        try {
            return put(IOUtils.toByteArray(stream));
        } catch (final IOException e) {
            throw RethrownException.rethrow(e);
        }
    }
}

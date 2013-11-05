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
import java.io.InputStream;
import java.util.Map;

import com.google.common.collect.MapMaker;
import com.google.inject.Singleton;

@Singleton
public final class InMemoryBlobStore extends BlobStoreBase {
    private final Map<BlobRef, byte[]> store = new MapMaker().makeMap();

    @Override
    public boolean contains(final BlobRef blob) {
        return store.containsKey(blob);
    }

    @Override
    protected void putByteArray(final BlobRef blobRef, final byte[] bytes) {
        store.put(blobRef, bytes);
    }

    @Override
    public InputStream getAsStream(final BlobRef blobRef) {
        final byte[] bytes = get(blobRef);
        return bytes == null ? null : new ByteArrayInputStream(store.get(blobRef));
    }

    @Override
    public byte[] get(final BlobRef blobRef) {
        return store.get(blobRef);
    }
}

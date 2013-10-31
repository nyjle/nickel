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
package org.nickelproject.lib.objectStore;

import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.lang.SerializationUtils;
import org.nickelproject.lib.blobStore.BlobRef;
import org.nickelproject.lib.blobStore.BlobStore;

public final class WrappedBlobStore implements ObjectStore {
    private final BlobStore blobStore;

    public WrappedBlobStore(final BlobStore blobStore) {
        this.blobStore = blobStore;
    }

    @Override
    public boolean contains(final BlobRef blobRef) {
        return blobStore.contains(blobRef);
    }

    @Override
    public BlobRef put(final Object pObject) {
        return blobStore.put(bytesFromObject((Serializable) pObject));
    }

    @Override
    public Object get(final BlobRef blobRef) {
        final InputStream stream = blobStore.getAsStream(blobRef);
        return stream == null ? null : objectFromStream(stream);
    }

    private byte[] bytesFromObject(final Serializable object) {
        return SerializationUtils.serialize(object);
    }

    private Object objectFromStream(final InputStream stream) {
        return SerializationUtils.deserialize(stream);
    }
}

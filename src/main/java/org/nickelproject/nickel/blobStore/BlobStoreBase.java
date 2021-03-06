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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.nickelproject.util.RethrownException;

import com.google.common.base.Preconditions;

public abstract class BlobStoreBase implements BlobStore {
    private final long checkContainsThreshold;
    
    public BlobStoreBase(final long checkContainsThreshold) {
        this.checkContainsThreshold = checkContainsThreshold;
    }
    
    @Override
    public final BlobRef put(final byte[] bytes) {
        Preconditions.checkNotNull(bytes);
        Preconditions.checkArgument(bytes.length > 0);
        final BlobRef key = BlobRef.keyFromBytes(bytes);
        if (bytes.length < checkContainsThreshold || !contains(key)) {
            putByteArray(key, bytes);
        }
        return key;
    }

    @Override
    public final BlobRef put(final InputStream stream) {
        Preconditions.checkNotNull(stream);
        try {
            return put(IOUtils.toByteArray(stream));
        } catch (final IOException e) {
            throw RethrownException.rethrow(e);
        }
    }

    protected abstract void putByteArray(BlobRef blobRef, byte[] bytes);
}

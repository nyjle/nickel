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
package org.nickelproject.nickel.externalReference;

import java.io.Serializable;

import org.nickelproject.nickel.blobStore.BlobRef;
import org.nickelproject.nickel.objectStore.ObjectStore;

import com.google.inject.Inject;

// The data must be immutable
public final class ExternalReference<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    @Inject private static ObjectStore objectStore;
    private final BlobRef            blobRef;

    private ExternalReference(final T data) {
        blobRef = objectStore.put(data);
    }

    public static <T> ExternalReference<T> of(final T data) {
        return new ExternalReference<T>(data);
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) objectStore.get(blobRef);
    }
    
    @Override
    public String toString() {
        return blobRef.toString();
    }
}

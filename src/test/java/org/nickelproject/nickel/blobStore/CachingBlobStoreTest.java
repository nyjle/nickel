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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.nio.charset.Charset;

import org.junit.Test;
import org.nickelproject.util.testUtil.UnitAnnotation;

import com.google.common.cache.CacheBuilderSpec;

@UnitAnnotation
public final class CachingBlobStoreTest extends BlobStoreTestBase {
    private final byte[] cachingTestBytes = "Caching Test".getBytes(Charset.forName("UTF-8"));

    private final BlobStore blobStore = 
            new CachingBlobStore(new InMemoryBlobStore(0), CacheBuilderSpec.parse(""));

    @Override
    protected BlobStore getBlobStore() {
        return blobStore;
    }

    @Test
    public void testCachingPut() {
        final BlobStore mock = createMock(BlobStore.class);
        final BlobRef blobRef = BlobRef.of("A_Blob_Reference");
        expect(mock.put(cachingTestBytes)).andReturn(blobRef);
        replay(mock);
        final BlobStore caching = new CachingBlobStore(mock, CacheBuilderSpec.parse(""));
        caching.put(cachingTestBytes);    // Should see put called once
        caching.get(blobRef);  // Should never see get called
        caching.get(blobRef);
        verify(mock);
    }
    
    @Test
    public void testCachingGet() {
        final BlobStore mock = createMock(BlobStore.class);
        final BlobRef blobRef = BlobRef.of("A_Blob_Reference");
        expect(mock.get(blobRef)).andReturn(cachingTestBytes);
        expect(mock.contains(blobRef)).andReturn(Boolean.TRUE);
        replay(mock);
        final BlobStore caching = new CachingBlobStore(mock, CacheBuilderSpec.parse(""));
        caching.get(blobRef);  // Should see get called only once
        caching.get(blobRef);
        verify(mock);
    }
}

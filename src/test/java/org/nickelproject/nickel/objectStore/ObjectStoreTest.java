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

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.nickel.blobStore.BlobRef;
import org.nickelproject.nickel.blobStore.InMemoryBlobStore;
import org.nickelproject.nickel.objectStore.CachingObjectStore;
import org.nickelproject.nickel.objectStore.ObjectStore;

public final class ObjectStoreTest {
    private final ObjectStore objectStore = new CachingObjectStore(new InMemoryBlobStore());
    private static final String testString  = "This is a test";

    @Test
    public void testContains() {
        final BlobRef blobRef = objectStore.put(testString);
        Assert.assertTrue(objectStore.contains(blobRef));
        Assert.assertFalse(objectStore.contains(BlobRef.of("fdafdas")));
    }

    @Test
    public void testRoundTrip() {
        String string = (String) objectStore.get(BlobRef.of("fafetateaewt"));
        Assert.assertNull(string);
        final BlobRef blobRef = objectStore.put(testString);
        string = (String) objectStore.get(blobRef);
        Assert.assertEquals(testString, string);
    }
}

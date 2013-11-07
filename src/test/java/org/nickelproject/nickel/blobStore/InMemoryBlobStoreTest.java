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
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public final class InMemoryBlobStoreTest {
    private final BlobStore blobStore = new InMemoryBlobStore();
    private final byte[]    testBytes = "This is a test".getBytes(Charset.forName("UTF-8"));

    @Test
    public void testContains() {
        final BlobRef blobRef = blobStore.put(testBytes);
        Assert.assertTrue(blobStore.contains(blobRef));
        Assert.assertFalse(blobStore.contains(BlobRef.of("fdafdas")));
    }

    @Test
    public void testRoundTrip() {
        byte[] bytes = blobStore.get(BlobRef.of("fafetateaewt"));
        Assert.assertNull(bytes);
        final BlobRef blobRef = blobStore.put(testBytes);
        bytes = blobStore.get(blobRef);
        Assert.assertTrue(Arrays.equals(bytes, testBytes));
    }

    @Test
    public void testStreamRoundTrip() throws Exception {
        byte[] bytes = blobStore.get(BlobRef.of("fateqwtq"));
        Assert.assertNull(bytes);
        final BlobRef blobRef = blobStore.put(new ByteArrayInputStream(testBytes));
        final InputStream stream = blobStore.getAsStream(blobRef);
        bytes = IOUtils.toByteArray(stream);
        Assert.assertTrue(Arrays.equals(bytes, testBytes));
    }
}

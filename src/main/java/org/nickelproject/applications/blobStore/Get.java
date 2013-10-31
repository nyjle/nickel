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
package org.nickelproject.applications.blobStore;

import org.nickelproject.lib.blobStore.BlobRef;
import org.nickelproject.lib.blobStore.BlobStore;
import org.nickelproject.lib.blobStore.S3BlobStore;

public final class Get {

    private Get() {
        // Prevents construction
    }

    public static void main(final String[] args) throws Exception {
        final String blobName = args[0];
        final BlobStore blobStore = new S3BlobStore();
        final byte[] bytes = blobStore.get(BlobRef.of(blobName));
        System.out.write(bytes);
    }
}

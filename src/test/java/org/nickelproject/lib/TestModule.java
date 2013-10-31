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
package org.nickelproject.lib;

import org.nickelproject.lib.blobStore.BlobStore;
import org.nickelproject.lib.blobStore.InMemoryBlobStore;
import org.nickelproject.lib.externalReference.ExternalReference;
import org.nickelproject.lib.objectStore.CachingObjectStore;
import org.nickelproject.lib.objectStore.ObjectStore;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public final class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(String.class)
            .annotatedWith(Names.named("BucketName"))
            .toInstance("BlobStore");
        bind(BlobStore.class).to(InMemoryBlobStore.class);
        bind(ObjectStore.class).to(CachingObjectStore.class);
        requestStaticInjection(ExternalReference.class);
    }
}

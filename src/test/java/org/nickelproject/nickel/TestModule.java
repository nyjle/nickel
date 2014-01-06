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
package org.nickelproject.nickel;

import org.nickelproject.nickel.blobStore.BlobStore;
import org.nickelproject.nickel.blobStore.InMemoryBlobStore;
import org.nickelproject.nickel.externalReference.ExternalReference;
import org.nickelproject.nickel.objectStore.CachingObjectStore;
import org.nickelproject.nickel.objectStore.ObjectStore;

import com.google.common.cache.CacheBuilderSpec;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

public final class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("BucketName")).toInstance("BlobStore");
        bind(BlobStore.class).to(InMemoryBlobStore.class);
        bind(ObjectStore.class).to(CachingObjectStore.class);
        requestStaticInjection(ExternalReference.class);
    }
    
    @Provides
    CacheBuilderSpec providesCacheBuilderSpec() {
        return CacheBuilderSpec.parse("softValues");
    }
}

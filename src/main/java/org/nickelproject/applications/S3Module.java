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
package org.nickelproject.applications;

import org.nickelproject.nickel.blobStore.BlobStore;
import org.nickelproject.nickel.blobStore.S3BlobStore;
import org.nickelproject.nickel.externalReference.ExternalReference;
import org.nickelproject.nickel.objectStore.CachingObjectStore;
import org.nickelproject.nickel.objectStore.ObjectStore;
import org.nickelproject.util.RetryProxy;
import org.nickelproject.util.sources.S3MultiFileSource;
import org.nickelproject.util.streamUtil.S3InputStreamFactory;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.common.cache.CacheBuilderSpec;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public final class S3Module extends AbstractModule {
    private static final long halfMegaByte = 500000L;
    private static final int s3BlobStoreThreadCount = 50;

    @Override
    protected void configure() {
        bind(Long.class).annotatedWith(Names.named("CheckContainsThreshold")).toInstance(halfMegaByte);
        bind(String.class).annotatedWith(Names.named("BucketName")).toInstance("BlobStoreNigel");
        bind(Integer.class).annotatedWith(Names.named("S3BlobStoreThreadCount"))
                            .toInstance(s3BlobStoreThreadCount);
        bind(ObjectStore.class).to(CachingObjectStore.class);
        requestStaticInjection(ExternalReference.class);
        requestStaticInjection(S3MultiFileSource.class);
        requestStaticInjection(S3InputStreamFactory.class);
    }
    
    @Provides @Singleton
    BlobStore provideBlobStore(final Injector injector) {
        return RetryProxy.newInstance(BlobStore.class, injector.getInstance(S3BlobStore.class));
    }
    
    @Provides @Singleton
    AmazonS3 provideS3Client() {
        return new AmazonS3Client(
                new AWSCredentialsProviderChain(
                        new DefaultAWSCredentialsProviderChain(),
                        new ClasspathPropertiesFileCredentialsProvider()));
    }
    
    @Provides
    CacheBuilderSpec providesCacheBuilderSpec() {
        final long maxCacheSize = 1024 * 1024 * 1024;
        return CacheBuilderSpec.parse("softValues,maximumWeight=" + maxCacheSize + ",recordStats");
    }
}

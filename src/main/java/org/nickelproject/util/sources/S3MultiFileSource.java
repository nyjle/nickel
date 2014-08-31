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
package org.nickelproject.util.sources;

import java.util.List;

import javax.annotation.Nonnull;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.nickel.types.RecordSource;
import org.nickelproject.util.csvUtil.CsvSource;
import org.nickelproject.util.streamUtil.InputStreamFactory;
import org.nickelproject.util.streamUtil.S3InputStreamFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public final class S3MultiFileSource<T> {
    @Inject private static AmazonS3 s3Client;
    
    private S3MultiFileSource() {
        // Prevents construction
    }
    
    public static Source<String> getS3BucketEntries(final String bucketName, final String key) {
        return Sources.from(listKeysInDirectory(bucketName, key));
    }
    
    public static Function<String, InputStreamFactory> s3InputStream(final String bucketName) {
        return new Function<String, InputStreamFactory>() {
            @Override
            public InputStreamFactory apply(@Nonnull final String partKey) {
                return new S3InputStreamFactory(bucketName, partKey);
            }
        };
    }
    
    public static Function<InputStreamFactory, Source<Record>> 
                                    asCsvRecords(final RecordDataType schema) {
        return new Function<InputStreamFactory, Source<Record>>() {
            @Override
            public Source<Record> apply(final InputStreamFactory inputStreamFactory) {
                return new CsvSource(inputStreamFactory, schema);
            }
        };
    }
    
    public static RecordSource getS3MultiFileSource(final String bucketName, final String key,
            final RecordDataType schema) {
        return new RecordSource(Sources.concat(
                Sources.transform(getS3BucketEntries(bucketName, key), 
                        Functions.compose(asCsvRecords(schema), s3InputStream(bucketName)))),
                schema);
    }
    
    private static List<String> listKeysInDirectory(final String bucketName, final String prefix) {
        final String delimiter = "/";
        final String fixedPrefix = prefix.endsWith(delimiter) ? prefix : prefix + delimiter;

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName).withPrefix(fixedPrefix).withDelimiter(delimiter);
        ObjectListing objects = s3Client.listObjects(listObjectsRequest);
        return Lists.transform(objects.getObjectSummaries(), new Function<S3ObjectSummary, String>() {
                @Override
                public String apply(@Nonnull final S3ObjectSummary input) {
                    return input.getKey();
                }            
            });
    }
}

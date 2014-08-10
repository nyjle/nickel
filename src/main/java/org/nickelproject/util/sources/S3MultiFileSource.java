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

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.util.CloseableIterator;
import org.nickelproject.util.csvUtil.CsvSource;
import org.nickelproject.util.streamUtil.S3InputStreamFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public final class S3MultiFileSource implements Source<Record> {
    private static final long serialVersionUID = 1L;
    private final String bucketName;
    private final String key;
    private final RecordDataType schema;
    @Inject private static AmazonS3 s3Client;
    
    public S3MultiFileSource(final String bucketName, final String key, final RecordDataType schema) {
        this.bucketName = bucketName;
        this.key = key;
        this.schema = schema;
    }
    
    @Override
    public int size() {
        return Source.unknownSize;
    }
    
    @Override
    public CloseableIterator<Record> iterator() {
        return Sources.concat(partition(0)).iterator();
    }

    @Override
    public Source<Source<Record>> partition(final int partitionSize) {
        final Source<String> keys = Sources.from(listKeysInDirectory(key));
        return Sources.transform(keys, new Function<String, Source<Record>>() {
                @Override
                public Source<Record> apply(final String partKey) {
                    return new CsvSource(new S3InputStreamFactory(bucketName, partKey), schema);
                }
            });
    }
    
    private List<String> listKeysInDirectory(final String prefix) {
        final String delimiter = "/";
        final String fixedPrefix = prefix.endsWith(delimiter) ? prefix : prefix + delimiter;

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName).withPrefix(fixedPrefix).withDelimiter(delimiter);
        ObjectListing objects = s3Client.listObjects(listObjectsRequest);
        return Lists.transform(objects.getObjectSummaries(), new Function<S3ObjectSummary, String>() {
                @Override
                public String apply(final S3ObjectSummary input) {
                    return input.getKey();
                }            
            });
    }
}

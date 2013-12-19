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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.util.RethrownException;
import org.nickelproject.util.csvUtil.CsvIterator;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.inject.Inject;

public final class S3CsvSource implements Source<Record> {
    private static final long serialVersionUID = 1L;
    private final String bucketName;
    private final String key;
    @Inject private static AmazonS3 s3Client;
    private final RecordDataType schema;
    
    public S3CsvSource(final String bucketName, final String key, final RecordDataType schema) {
        this.bucketName = bucketName;
        this.key = key;
        this.schema = schema;
    }
    
    @Override
    public Iterator<Record> iterator() {
        final S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key));
        final InputStream objectData = object.getObjectContent();
        try {
            return new CsvIterator(new InputStreamReader(objectData, "UTF-8"), schema);
        } catch (UnsupportedEncodingException e) {
            throw RethrownException.rethrow(e);
        }
    }

    @Override
    public Source<? extends Source<Record>> partition(final int partitionSize) {
        return Sources.singleton(this);
    }
}

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
package org.nickelproject.util.streamUtil;

import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.inject.Inject;

public final class S3InputStreamFactory implements InputStreamFactory {
    private static final long serialVersionUID = 1L;
    private final String bucketName;
    private final String key;
    @Inject private static AmazonS3 s3Client;
    
    public S3InputStreamFactory(final String bucketName, final String key) {
        this.bucketName = bucketName;
        this.key = key;
    }

    @Override
    public InputStream getInputStream() {
        final S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key));
        return object.getObjectContent();        
    }
}

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
package org.nickelproject.lib.blobStore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.nickelproject.lib.util.RethrownException;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3BlobStore extends BlobStoreBase {
    private static final String   bucketName = "BlobStore";
    private static final AmazonS3 s3Client   = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());

    @Override
    public final byte[] get(final BlobRef blobRef) {
        try {
            final InputStream inputStream = getAsStream(blobRef);
            return inputStream == null ? null : IOUtils.toByteArray(getAsStream(blobRef));
        } catch (final IOException e) {
            throw RethrownException.rethrow(e);
        }
    }

    @Override
    public final boolean contains(final BlobRef blobRef) {
        boolean retVal = true;
        try {
            s3Client.getObjectMetadata(bucketName, blobRef.toString());
        } catch (final AmazonS3Exception e) {
            retVal = false;
        }
        return retVal;
    }

    @Override
    public final InputStream getAsStream(final BlobRef blobRef) {
        return contains(blobRef) ? s3Client.getObject(bucketName, blobRef.toString()).getObjectContent() : null;
    }

    @Override
    public final void putByteArray(final BlobRef blobRef, final byte[] pBytes) {
        final ByteArrayInputStream vByteArrayInputStream = new ByteArrayInputStream(pBytes);
        final ObjectMetadata vMetadata = new ObjectMetadata();
        vMetadata.setContentLength(pBytes.length);
        s3Client.putObject(new PutObjectRequest(bucketName, blobRef.toString(), vByteArrayInputStream, vMetadata));
    }
}

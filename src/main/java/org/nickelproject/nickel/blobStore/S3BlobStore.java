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
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.nickelproject.util.RethrownException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class S3BlobStore extends BlobStoreBase {
    private static final int notFoundCode = 404;
    private final AmazonS3 s3Client;
    private final String bucketName;

    @Inject
    S3BlobStore(final AmazonS3 s3Client,
            @Named("BucketName") final String bucketName,
            @Named("CheckContainsThreshold") final long checkContainsThreshold) {
        super(checkContainsThreshold);
        this.bucketName = bucketName;
        this.s3Client = s3Client;
    }

    @Override
    public final byte[] get(final BlobRef blobRef) {
        InputStream inputStream = null;
        try {
            inputStream = getAsStream(blobRef);
            return inputStream == null ? null : IOUtils.toByteArray(inputStream);
        } catch (final IOException e) {
            throw RethrownException.rethrow(e);
        } finally {
           if (inputStream != null) {
               try {
                   inputStream.close();
               } catch (final IOException e) {
                   throw RethrownException.rethrow(e);
               }
           }
        }
    }

    @Override
    public final boolean contains(final BlobRef blobRef) {
        boolean retVal = true;
        try {
            s3Client.getObjectMetadata(bucketName, blobRef.toString());
        } catch (final AmazonS3Exception e) {
            if (e.getStatusCode() == notFoundCode) {
                retVal = false;
            } else {
                throw RethrownException.rethrow(e);
            }
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

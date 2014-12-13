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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.nickelproject.util.RethrownException;
import org.nickelproject.util.RetryProxy;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class S3BlobStore extends BlobStoreBase {
    private static final long downloadPartSize = 5000000L;
    private static final long uploadPartSize = 5 * 1024 * 1024;
    private static final long multiPartThreshold = uploadPartSize * 4;
    private static final int notFoundCode = 404;
    private final AmazonS3 s3Client;
    private final String bucketName;
    private final ExecutorService executor = Executors.newFixedThreadPool(50, 
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("S3BlobStore-%s").build());

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

    private ObjectMetadata getMetadata(final BlobRef blobRef) {
        ObjectMetadata retVal = null;
        try {
            retVal = s3Client.getObjectMetadata(bucketName, blobRef.toString());
        } catch (final AmazonS3Exception e) {
            if (e.getStatusCode() != notFoundCode) {
                throw RethrownException.rethrow(e);
            }
        }
        return retVal;        
    }
    
    @Override
    public final boolean contains(final BlobRef blobRef) {
        return getMetadata(blobRef) != null;
    }

    @Override
    public final InputStream getAsStream(final BlobRef blobRef) {
        final ObjectMetadata metaData = getMetadata(blobRef);
        if (metaData != null) {
            long start = 0;
            final List<ByteSource> byteSources = Lists.newArrayList();
            for (; start + downloadPartSize + 1 < metaData.getInstanceLength(); 
                    start += downloadPartSize + 1) {
                byteSources.add(
                        new FutureByteSource(
                                executor.submit(
                                        new GetPartCallable(blobRef, start, start + downloadPartSize))));
            }
            byteSources.add(
                    new FutureByteSource(
                            executor.submit(
                                    new GetPartCallable(blobRef, start, metaData.getInstanceLength() - 1))));
            try {
                return ByteSource.concat(byteSources).openStream();
            } catch (IOException e) {
                throw RethrownException.rethrow(e);
            }
        } else {
            return null;
        }
    }

    @Override
    public final void putByteArray(final BlobRef blobRef, final byte[] pBytes) {
        if (pBytes.length < multiPartThreshold) {
            putSinglePartByteArray(blobRef, pBytes);
        } else {
            putMultiPartByteArray(blobRef, pBytes);
        }
    }
    
    private void putSinglePartByteArray(final BlobRef blobRef, final byte[] pBytes) {
        final ByteArrayInputStream vByteArrayInputStream = new ByteArrayInputStream(pBytes);
        final ObjectMetadata vMetadata = new ObjectMetadata();
        vMetadata.setContentLength(pBytes.length);
        s3Client.putObject(
                new PutObjectRequest(bucketName, blobRef.toString(), vByteArrayInputStream, vMetadata));
    }

    
    private void putMultiPartByteArray(final BlobRef blobRef, final byte[] pBytes) {
        final CompletionService<PartETag> completionService = 
                new ExecutorCompletionService<PartETag>(executor);
        final String key = blobRef.toString();
        final PartTransferInterface partGetter = 
                RetryProxy.newInstance(PartTransferInterface.class, new PartTransfer());
        final String uploadId = partGetter.initiateUpload(key);
        int partNumber = 1;
        for (int start = 0; start < pBytes.length; start += uploadPartSize) {
            final long length = Math.min(uploadPartSize, pBytes.length - start);
            completionService.submit(
                    new PutPartCallable(start, (int) length, pBytes, partNumber++, key, uploadId,
                            start + uploadPartSize >= pBytes.length));
        }
        final List<PartETag> partETags = Lists.newArrayList();
        for (int i = 1; i < partNumber; i++) {
            try {
                partETags.add(completionService.take().get());
            } catch (final InterruptedException e1) {
                Thread.currentThread().interrupt();
                throw RethrownException.rethrow(e1);
            } catch (final ExecutionException e2) {
                throw RethrownException.rethrow(e2);
            }
        }
        partGetter.completeUpload(key, uploadId, partETags);
    }
    
    private final class PutPartCallable implements Callable<PartETag> {
        private final int start;
        private final int length;
        private final byte[] bytes;
        private final int partNumber;
        private final String key;
        private final String uploadId;
        private final boolean isLast;
        
        private PutPartCallable(final int start, final int length, final byte[] bytes, final int partNumber,
                final String key, final String uploadId, final boolean isLast) {
            this.start = start;
            this.length = length;
            this.bytes = bytes;
            this.partNumber = partNumber;
            this.key = key;
            this.uploadId = uploadId;
            this.isLast = isLast;
        }
        
        @Override
        public PartETag call() throws Exception {
            final PartTransferInterface partGetter = 
                    RetryProxy.newInstance(PartTransferInterface.class, new PartTransfer());
            final ByteArrayInputStream stream = new ByteArrayInputStream(bytes, start, length);
            return partGetter.putPart(key, uploadId, partNumber, stream, isLast, length);
        } 
    }
    
    private final class GetPartCallable implements Callable<InputStream> {
        private final long start;
        private final long end;
        private final BlobRef blobRef;

        public GetPartCallable(final BlobRef blobRef, final long start, final long end) {
            this.blobRef = blobRef;
            this.start = start;
            this.end = end;
        }
        
        @Override
        public InputStream call() throws Exception {
            PartTransferInterface partGetter = 
                    RetryProxy.newInstance(PartTransferInterface.class, new PartTransfer());            
            GetObjectRequest rangeRequest = new GetObjectRequest(bucketName, blobRef.toString());
            rangeRequest.setRange(start, end);
            return new ByteArrayInputStream(partGetter.getPart(rangeRequest));
        }     
    }
    
    public interface PartTransferInterface {
        byte[] getPart(GetObjectRequest rangeRequest);
        PartETag putPart(String key, String uploadId, int partNumber, 
                ByteArrayInputStream bytes, boolean isLast, int size);
        String completeUpload(String key, String uploadId, List<PartETag> partETags);
        String initiateUpload(String key);        
    }
    
    private final class PartTransfer implements PartTransferInterface {
        
        @Override
        public byte[] getPart(final GetObjectRequest rangeRequest) {
            final InputStream inputStream = s3Client.getObject(rangeRequest).getObjectContent();
            if (inputStream == null) {
                return null;
            }
            try {
                return IOUtils.toByteArray(inputStream);
            } catch (final Exception e) {
                throw RethrownException.rethrow(e);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw RethrownException.rethrow(e);
                }
            }
        }        
        
        @Override
        public String initiateUpload(final String key) {
            final InitiateMultipartUploadRequest uploadRequest = 
                    new InitiateMultipartUploadRequest(bucketName, key);
            InitiateMultipartUploadResult result = s3Client.initiateMultipartUpload(uploadRequest);
            return result.getUploadId();
        }
        
        @Override
        public String completeUpload(final String key, 
                final String uploadId, final List<PartETag> partETags) {
            final CompleteMultipartUploadRequest request = 
                    new CompleteMultipartUploadRequest(bucketName, key, uploadId, partETags);
            CompleteMultipartUploadResult result = s3Client.completeMultipartUpload(request);
            return result.getETag();
        }
        
        @Override
        public PartETag putPart(final String key, final String uploadId,
                final int partNumber, final ByteArrayInputStream bytes, final boolean isLast, final int size) {
            final UploadPartRequest request = new UploadPartRequest();
            request.setBucketName(bucketName);
            request.setKey(key);
            request.setInputStream(bytes);
            request.setUploadId(uploadId);
            request.setPartNumber(partNumber);
            request.setLastPart(isLast);
            request.setPartSize(size);
            final UploadPartResult result = s3Client.uploadPart(request);
            return new PartETag(result.getPartNumber(), result.getETag());
        }
    }
    
    private static final class FutureByteSource extends ByteSource {
        private final Future<InputStream> future;
        
        public FutureByteSource(final Future<InputStream> future) {
            this.future = future;
        }
        
        @Override
        public InputStream openStream() throws IOException {
            try {
                return future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw RethrownException.rethrow(e);
            } catch (ExecutionException e) {
                throw RethrownException.rethrow(e);
            }
        }  
    }
}

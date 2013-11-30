package org.nickelproject.util.sources;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public final class S3MultiFileSource implements Source<Record> {
    private static final long serialVersionUID = 1L;
    private final String bucketName;
    private final String key;
    private final AmazonS3 s3Client;
    private final RecordDataType schema;
    
    public S3MultiFileSource(final String bucketName, final String key, final AmazonS3 s3Client,
            final RecordDataType schema) {
        this.bucketName = bucketName;
        this.key = key;
        this.s3Client = s3Client;
        this.schema = schema;
    }
    
    @Override
    public Iterator<Record> iterator() {
        return Sources.concat(partition(0)).iterator();
    }

    @Override
    public Source<Source<Record>> partition(final int partitionSize) {
        final Source<String> keys = Sources.from(listKeysInDirectory(bucketName, key));
        return Sources.transform(keys, new Function<String, Source<Record>>() {
                @Override
                public Source<Record> apply(final String key) {
                    return new S3CsvSource(bucketName, key, s3Client, schema);
                }
            });
    }
    
    private final List<String> listKeysInDirectory(final String bucketName, String prefix) {
        String delimiter = "/";
        if (!prefix.endsWith(delimiter)) {
            prefix += delimiter;
        }

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName).withPrefix(prefix).withDelimiter(delimiter);
        ObjectListing objects = s3Client.listObjects(listObjectsRequest);
        return Lists.transform(objects.getObjectSummaries(), new Function<S3ObjectSummary, String>() {
                @Override
                @Nullable
                public String apply(@Nullable S3ObjectSummary input) {
                    return input.getKey();
                }            
            });
    }
}

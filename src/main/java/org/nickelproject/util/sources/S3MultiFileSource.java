package org.nickelproject.util.sources;

import java.util.Iterator;
import java.util.List;

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
import com.google.inject.Inject;

public final class S3MultiFileSource implements Source<Record> {
    private static final long serialVersionUID = 1L;
    private final String bucketName;
    private final String key;
    @Inject private static AmazonS3 s3Client;
    private final RecordDataType schema;
    
    public S3MultiFileSource(final String bucketName, final String key, final RecordDataType schema) {
        this.bucketName = bucketName;
        this.key = key;
        this.schema = schema;
    }
    
    @Override
    public Iterator<Record> iterator() {
        return Sources.concat(partition(0)).iterator();
    }

    @Override
    public Source<Source<Record>> partition(final int partitionSize) {
        final Source<String> keys = Sources.from(listKeysInDirectory(key));
        return Sources.transform(keys, new Function<String, Source<Record>>() {
                @Override
                public Source<Record> apply(final String partKey) {
                    return new S3CsvSource(bucketName, partKey, schema);
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

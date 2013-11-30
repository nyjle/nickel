package org.nickelproject.util.sources;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.util.csvUtil.CsvIterator;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public final class S3CsvSource implements Source<Record> {
    private static final long serialVersionUID = 1L;
    private final String bucketName;
    private final String key;
    private final AmazonS3 s3Client;
    private final RecordDataType schema;
    
    public S3CsvSource(final String bucketName, final String key, final AmazonS3 s3Client,
            final RecordDataType schema) {
        this.bucketName = bucketName;
        this.key = key;
        this.s3Client = s3Client;
        this.schema = schema;
    }
    
    @Override
    public Iterator<Record> iterator() {
        final S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key));
        final InputStream objectData = object.getObjectContent();
        return new CsvIterator(new InputStreamReader(objectData), schema);
    }

    @Override
    public Source<? extends Source<Record>> partition(final int partitionSize) {
        return Sources.singleton(this);
    }
}

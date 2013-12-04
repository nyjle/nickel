package org.nickelproject.util.csvUtil;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.nickelproject.nickel.RecordSource;
import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.util.RethrownException;

public final class CsvFileSource implements RecordSource {
    private static final long serialVersionUID = 1L;
    private final String fileName;
    private final RecordDataType schema;

    public CsvFileSource(final String fileName) {
        this.fileName = fileName;
        try {
            this.schema = CsvIterator.readSchema(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
        } catch (final Exception e) {
            throw RethrownException.rethrow(e);
        }
    }

    @Override
    public Iterator<Record> iterator() {
        try {
            return new CsvIterator(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
        } catch (final Exception e) {
            throw RethrownException.rethrow(e);
        }
    }

    @Override
    public RecordDataType schema() {
        return schema;
    }

    @Override
    public Source<? extends Source<Record>> partition(final int partitionSize) {
        return Sources.singleton(this);
    }
}

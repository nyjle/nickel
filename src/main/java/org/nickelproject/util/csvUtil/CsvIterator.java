package org.nickelproject.util.csvUtil;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.util.RethrownException;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

public final class CsvIterator implements Iterator<Record> {
    private final ICsvListReader  listReader;
    private Record                data;
    private final RecordDataType  schema;
    private final CellProcessor[] cellProcessors;

    public static RecordDataType readSchema(final Reader reader) {
        ICsvListReader listReader = null;
        final RecordDataType schema;
        try {
            listReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE);
            schema = Header.parseHeader(listReader.getHeader(true));
        } catch (final Exception e) {
            throw RethrownException.rethrow(e);
        } finally {
            if (listReader != null) {
                try {
                    listReader.close();
                } catch (final Exception e) {
                    throw RethrownException.rethrow(e);
                }
            }
        }
        return schema;
    }
    
    public CsvIterator(final Reader reader) {
        try {
            listReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE);
            schema = Header.parseHeader(listReader.getHeader(true));
            cellProcessors = Header.getCellProcessors(schema);
            final List<Object> fields = listReader.read(cellProcessors);
            data = fields == null ? null : Record.fromList(schema, fields);
        } catch (final Exception e) {
            throw RethrownException.rethrow(e);
        }
    }

    public CsvIterator(final Reader reader, final RecordDataType schema) {
        this.schema = schema;
        try {
            listReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE);
            cellProcessors = Header.getCellProcessors(schema);
            final List<Object> fields = listReader.read(cellProcessors);
            data = fields == null ? null : Record.fromList(schema, fields);
        } catch (final Exception e) {
            throw RethrownException.rethrow(e);
        }
    }
    
    @Override
    public boolean hasNext() {
        return data != null;
    }

    @Override
    public Record next() {
        final Record retVal = data;
        try {
            final List<Object> fields = listReader.read(cellProcessors);
            data = fields == null ? null : Record.fromList(schema, fields);
        } catch (final IOException e) {
            throw RethrownException.rethrow(e);
        }
        return retVal;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

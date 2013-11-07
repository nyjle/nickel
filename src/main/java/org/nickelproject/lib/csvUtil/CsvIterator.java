package org.nickelproject.lib.csvUtil;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import org.nickelproject.lib.types.Record;
import org.nickelproject.lib.types.RecordDataType;
import org.nickelproject.lib.util.RethrownException;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

class CsvIterator implements Iterator<Record> {
    private final ICsvListReader  listReader;
    private Record                data;
    private final RecordDataType  schema;
    private final CellProcessor[] cellProcessors;

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

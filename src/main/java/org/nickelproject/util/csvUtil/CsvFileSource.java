package org.nickelproject.util.csvUtil;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.nickelproject.nickel.types.Record;
import org.nickelproject.util.RethrownException;

public class CsvFileSource implements Iterable<Record> {
    private final String fileName;

    public CsvFileSource(final String fileName) {
        this.fileName = fileName;
    }

    @Override
    public final Iterator<Record> iterator() {
        try {
            return new CsvIterator(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
        } catch (final Exception e) {
            throw RethrownException.rethrow(e);
        }
    }
}
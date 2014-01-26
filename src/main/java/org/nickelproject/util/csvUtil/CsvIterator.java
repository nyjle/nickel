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
package org.nickelproject.util.csvUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.util.RethrownException;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.google.common.collect.UnmodifiableIterator;

public final class CsvIterator extends UnmodifiableIterator<Record> {
    private final ICsvListReader  listReader;
    private Record                data;
    private final RecordDataType  schema;
    private final CellProcessor[] cellProcessors;

    public static RecordDataType readSchema(final InputStream inputStream) {
        ICsvListReader listReader = null;
        final RecordDataType schema;
        try {
            final Reader reader = new InputStreamReader(inputStream, "UTF-8");
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
}

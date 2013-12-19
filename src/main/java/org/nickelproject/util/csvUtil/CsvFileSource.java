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

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.nickel.types.RecordSource;
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

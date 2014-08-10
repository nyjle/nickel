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

import java.io.InputStreamReader;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.nickel.types.RecordSource;
import org.nickelproject.util.CloseableIterator;
import org.nickelproject.util.RethrownException;
import org.nickelproject.util.streamUtil.InputStreamFactory;

public final class CsvSource implements RecordSource {
    private static final long serialVersionUID = 1L;
    private final InputStreamFactory inputStreamFactory;
    private final RecordDataType schema;

    public CsvSource(final InputStreamFactory inputStreamFactory) {
        this(inputStreamFactory, null);
    }

    public CsvSource(final InputStreamFactory inputStreamFactory, final RecordDataType schema) {
        this.inputStreamFactory = inputStreamFactory;
        this.schema = schema;
    }

    @Override
    public int size() {
        return Source.unknownSize;
    }
    
    @Override
    public CloseableIterator<Record> iterator() {
        try {
            return schema == null 
                    ? new CsvIterator(
                            new InputStreamReader(inputStreamFactory.getInputStream(), "UTF-8"))
                    : new CsvIterator(
                            new InputStreamReader(inputStreamFactory.getInputStream(), "UTF-8"), schema);
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

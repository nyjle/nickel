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
package org.nickelproject.nickel.types;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.util.CloseableIterator;

import com.google.common.base.Function;


public final class RecordSource implements Source<Record> {
    private static final long serialVersionUID = 1L;
    private final Source<Record> source;
    private final RecordDataType schema;
    
    public RecordSource(final Source<Record> source, final RecordDataType schema) {
        this.source = source;
        this.schema = schema;
    }
    
    public RecordDataType schema() {
        return schema;
    }

    @Override
    public CloseableIterator<Record> iterator() {
        return source.iterator();
    }

    @Override
    public Source<? extends Source<Record>> partition(final int sizeGuideline) {
        return Sources.transform(source.partition(sizeGuideline), toRecordSource(schema));
    }

    @Override
    public int size() {
        return source.size();
    }
    
    private static Function<Source<Record>, RecordSource> toRecordSource(final RecordDataType schema) {
        return new Function<Source<Record>, RecordSource>() {
            @Override
            public RecordSource apply(final Source<Record> input) {
                return new RecordSource(input, schema);
            }
        };
    }
}

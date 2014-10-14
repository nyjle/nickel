/*
 * Copyright (c) 2013, 2014 Nigel Duffy
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
package org.nickelproject.util.functions;

import java.io.Serializable;

import javax.annotation.Nonnull;

import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;

public final class PairToRecord<A, B> implements Function<Pair<A, B>, Record>, Serializable {
    private final RecordDataType schema;
    
    public PairToRecord(final RecordDataType schema) {
        this.schema = schema;
    }
    
    @Override
    public Record apply(@Nonnull final Pair<A, B> input) {
        return Record.of(schema, input.getA(), input.getB());
    }
}

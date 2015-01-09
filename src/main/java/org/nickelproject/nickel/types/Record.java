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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * A Record is the equivalent of a structure in C, or a JSON structure.
 * It contains fields each of which has a type and a name.
 * Fields are accessible only via their index for performance reasons.
 * The preferred pattern is to obtain the index from the schema (really the type of the record)
 * and then to use that to access the fields.
 */
public final class Record implements Serializable {
    private static final long serialVersionUID = 1L;
    private final RecordDataType schema;
    private final Object[]       fields;

    private Record(final RecordDataType schema, final Object... fields) {
        this.schema = schema;
        this.fields = fields;
    }

    public static Record of(final RecordDataType schema, final Object... fields) {
        return new Record(schema, fields);
    }

    public static Record fromList(final RecordDataType schema, final List<Object> fields) {
        return new Record(schema, fields.toArray(new Object[0]));
    }

    public Object getField(final int index) {
        return fields[index];
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("EI_EXPOSE_REP")
    public Object[] getFields() {
        return fields;
    }

    public RecordDataType getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return Arrays.toString(fields);
    }
}

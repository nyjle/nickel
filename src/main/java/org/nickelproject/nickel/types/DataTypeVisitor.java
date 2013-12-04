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

public abstract class DataTypeVisitor<S, T> {
    public final S visit(final DataType dataType, final T data) {
        return dataType.visit(this, data);
    }

    protected abstract S visit(IntegerDataType dataType, T data);
    protected abstract S visit(DoubleDataType dataType, T data);
    protected abstract S visit(StringDataType dataType, T data);
    protected abstract S visit(ByteArrayDataType dataType, T data);
    protected abstract S visit(RecordDataType dataType, T data);
}

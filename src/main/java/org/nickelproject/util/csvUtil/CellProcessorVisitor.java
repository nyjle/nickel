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

import org.nickelproject.nickel.types.ByteArrayDataType;
import org.nickelproject.nickel.types.DataTypeVisitor;
import org.nickelproject.nickel.types.DoubleDataType;
import org.nickelproject.nickel.types.IntegerDataType;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.nickel.types.StringDataType;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.ift.CellProcessor;

public final class CellProcessorVisitor extends DataTypeVisitor<CellProcessor, Void> {
    @Override
    protected CellProcessor visit(final IntegerDataType dataType, final Void data) {
        return new Trim(new ParseInt());
    }

    @Override
    protected CellProcessor visit(final StringDataType dataType, final Void data) {
        return new Trim();
    }

    @Override
    protected CellProcessor visit(final ByteArrayDataType dataType, final Void data) {
        throw new RuntimeException("Cannot read a byte array");
    }

    @Override
    protected CellProcessor visit(final RecordDataType dataType, final Void data) {
        throw new RuntimeException();
    }

    @Override
    protected CellProcessor visit(final DoubleDataType dataType, final Void data) {
        return new Trim(new ParseDouble());
    }
}

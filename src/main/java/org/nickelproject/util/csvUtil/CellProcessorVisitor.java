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

import org.nickelproject.nickel.types.ByteArrayDataType;
import org.nickelproject.nickel.types.DataTypeVisitor;
import org.nickelproject.nickel.types.DoubleDataType;
import org.nickelproject.nickel.types.IntegerDataType;
import org.nickelproject.nickel.types.JavaClassDataType;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.nickel.types.StringDataType;
import org.nickelproject.util.IoUtil;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

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

    @Override
    protected CellProcessor visit(final JavaClassDataType dataType, final Void data) {
        return new Trim(new ParseObject());
    }
    
    private static final class ParseObject extends CellProcessorAdaptor implements StringCellProcessor {
        
        public ParseObject() {
                super();
        }
        
        public ParseObject(final CellProcessor next) {
                // this constructor allows other processors to be chained after ParseDay
                super(next);
        }
        
        public Object execute(final Object value, final CsvContext context) {                
            validateInputNotNull(value, context);  // throws an Exception if the input is null
            
            Object javaObject;
            try {
                javaObject = IoUtil.deserhex((String) value);
                return next.execute(javaObject, context);
            } catch (IOException e) {
                throw new SuperCsvCellProcessorException(
                        String.format("Could not parse '%s' as a Java object", value), context, this);
            }
        }
    }
}

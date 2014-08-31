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

import java.util.List;

import org.nickelproject.nickel.types.DataType;
import org.nickelproject.nickel.types.DoubleDataType;
import org.nickelproject.nickel.types.IntegerDataType;
import org.nickelproject.nickel.types.JavaClassDataType;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.nickel.types.StringDataType;
import org.nickelproject.util.tuple.Pair;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public final class Header {

    private Header() {
        // Prevents construction
    }

    public static List<String> createHeader(final RecordDataType recordDataType) {
        final List<String> retVal = Lists.newArrayList();
        for (final Pair<String, DataType> pair : recordDataType.getMembers()) {
            retVal.add(pair.getA() + ":" + DataTypeNameVisitor.visitStatic(pair.getB()));
        }
        return retVal;
    }

    public static RecordDataType parseHeader(final String[] fields) {
        final RecordDataType.Builder builder = new RecordDataType.Builder();
        for (final String field : fields) {
            final String[] splits = field.split(":");
            final String name = splits[0];
            final String typeString = splits.length == 2 ? splits[1] : "String";
            final DataType dataType = parseDataType(typeString);
            builder.add(name, dataType);
        }
        return builder.build();
    }

    private static DataType parseDataType(final String dataTypeString) {
        DataType dataType = null;
        if (dataTypeString.equals("Int")) {
            dataType = new IntegerDataType();
        } else if (dataTypeString.equals("String")) {
            dataType = new StringDataType();
        } else if (dataTypeString.equals("Double")) {
            dataType = new DoubleDataType();
        } else {
            dataType = new JavaClassDataType(dataTypeString);
        }
        Preconditions.checkState(dataType != null);
        return dataType;
    }

    public static CellProcessor[] getCellProcessors(final RecordDataType schema) {
        final List<CellProcessor> vRetVal = Lists.newArrayList();
        final CellProcessorVisitor visitor = new CellProcessorVisitor();
        for (final Pair<String, DataType> pair : schema.getMembers()) {
            vRetVal.add(visitor.visit(pair.getB(), null));
        }
        return vRetVal.toArray(new CellProcessor[0]);
    }
}

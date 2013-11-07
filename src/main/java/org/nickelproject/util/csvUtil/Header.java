package org.nickelproject.util.csvUtil;

import java.util.List;

import org.nickelproject.nickel.types.DataType;
import org.nickelproject.nickel.types.DoubleDataType;
import org.nickelproject.nickel.types.IntegerDataType;
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
            final String typeString = splits[1];
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
        }
        Preconditions.checkState(dataType != null);
        return dataType;
    }

    public static CellProcessor[] getCellProcessors(final RecordDataType schema) {
        final List<CellProcessor> vRetVal = Lists.newArrayList();
        final CellProcessorVisitor visitor = new CellProcessorVisitor();
        for (final Pair<String, DataType> pair : schema.getMembers()) {
            vRetVal.add(visitor.visit(pair.getB()));
        }
        return vRetVal.toArray(new CellProcessor[0]);
    }
}

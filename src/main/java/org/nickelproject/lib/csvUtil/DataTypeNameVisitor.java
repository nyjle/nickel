package org.nickelproject.lib.csvUtil;

import org.nickelproject.lib.types.ByteArrayDataType;
import org.nickelproject.lib.types.DataType;
import org.nickelproject.lib.types.DataTypeVisitor;
import org.nickelproject.lib.types.DoubleDataType;
import org.nickelproject.lib.types.IntegerDataType;
import org.nickelproject.lib.types.RecordDataType;
import org.nickelproject.lib.types.StringDataType;

public final class DataTypeNameVisitor extends DataTypeVisitor<String> {

    private DataTypeNameVisitor() {
        // Ensure never constructed
    }

    public static String visitStatic(final DataType dataType) {
        return new DataTypeNameVisitor().visit(dataType);
    }

    @Override
    protected String visit(final IntegerDataType dataType) {
        return "Int";
    }

    @Override
    protected String visit(final StringDataType dataType) {
        return "String";
    }

    @Override
    protected String visit(final ByteArrayDataType dataType) {
        return "ByteArray";
    }

    @Override
    protected String visit(final RecordDataType dataType) {
        throw new RuntimeException();
    }

    @Override
    protected String visit(final DoubleDataType dataType) {
        return "Double";
    }
}

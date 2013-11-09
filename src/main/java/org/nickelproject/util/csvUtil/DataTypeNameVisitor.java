package org.nickelproject.util.csvUtil;

import org.nickelproject.nickel.types.ByteArrayDataType;
import org.nickelproject.nickel.types.DataType;
import org.nickelproject.nickel.types.DataTypeVisitor;
import org.nickelproject.nickel.types.DoubleDataType;
import org.nickelproject.nickel.types.IntegerDataType;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.nickel.types.StringDataType;

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

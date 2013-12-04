package org.nickelproject.util.csvUtil;

import org.nickelproject.nickel.types.ByteArrayDataType;
import org.nickelproject.nickel.types.DataType;
import org.nickelproject.nickel.types.DataTypeVisitor;
import org.nickelproject.nickel.types.DoubleDataType;
import org.nickelproject.nickel.types.IntegerDataType;
import org.nickelproject.nickel.types.RecordDataType;
import org.nickelproject.nickel.types.StringDataType;

public final class DataTypeNameVisitor extends DataTypeVisitor<String, Void> {

    private DataTypeNameVisitor() {
        // Ensure never constructed
    }

    public static String visitStatic(final DataType dataType) {
        return new DataTypeNameVisitor().visit(dataType, null);
    }

    @Override
    protected String visit(final IntegerDataType dataType, final Void data) {
        return "Int";
    }

    @Override
    protected String visit(final StringDataType dataType, final Void data) {
        return "String";
    }

    @Override
    protected String visit(final ByteArrayDataType dataType, final Void data) {
        return "ByteArray";
    }

    @Override
    protected String visit(final RecordDataType dataType, final Void data) {
        throw new RuntimeException();
    }

    @Override
    protected String visit(final DoubleDataType dataType, final Void data) {
        return "Double";
    }
}

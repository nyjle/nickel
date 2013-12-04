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

package org.nickelproject.lib.csvUtil;

import org.nickelproject.lib.types.ByteArrayDataType;
import org.nickelproject.lib.types.DataTypeVisitor;
import org.nickelproject.lib.types.DoubleDataType;
import org.nickelproject.lib.types.IntegerDataType;
import org.nickelproject.lib.types.RecordDataType;
import org.nickelproject.lib.types.StringDataType;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.ift.CellProcessor;

public final class CellProcessorVisitor extends DataTypeVisitor<CellProcessor> {
    @Override
    protected CellProcessor visit(final IntegerDataType dataType) {
        return new Trim(new ParseInt());
    }

    @Override
    protected CellProcessor visit(final StringDataType dataType) {
        return new Trim();
    }

    @Override
    protected CellProcessor visit(final ByteArrayDataType dataType) {
        throw new RuntimeException("Cannot read a byte array");
    }

    @Override
    protected CellProcessor visit(final RecordDataType dataType) {
        throw new RuntimeException();
    }

    @Override
    protected CellProcessor visit(final DoubleDataType dataType) {
        return new Trim(new ParseDouble());
    }
}

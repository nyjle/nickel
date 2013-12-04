package org.nickelproject.nickel.types;

import org.nickelproject.nickel.types.RecordDataType.Builder;

public final class DataTypes {

    private DataTypes() {
        // Prevents construction
    }
    
    public static RecordDataType projectSchema(final RecordDataType schema, final String... fieldNames) {
        Builder builder = new RecordDataType.Builder();
        for (final String name : fieldNames) {
            builder.add(name, schema.getType(schema.getIndex(name)));
        }
        return builder.build();
    }
    
    public static int[] indices(final RecordDataType schema, final String... fieldNames) {
        final int[] retVal = new int[fieldNames.length];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = schema.getIndex(fieldNames[i]);
        }
        return retVal;
    }
}

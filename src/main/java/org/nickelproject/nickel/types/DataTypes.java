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
}

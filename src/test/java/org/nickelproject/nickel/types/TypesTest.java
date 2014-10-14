/*
 * Copyright (c) 2013, 2014 Nigel Duffy
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
package org.nickelproject.nickel.types;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nickelproject.util.testUtil.UnitAnnotation;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

@UnitAnnotation
public final class TypesTest {
    private RecordDataType schema;
    
    @Before
    public void initialize() {
        schema = new RecordDataType.Builder()
                                .add("Field1", new DoubleDataType())
                                .add("Field2", new IntegerDataType())
                                .add("Field3", new IntegerDataType())
                                .build();        
    }
    
    @Test
    public void indicesTest() {
        final int[] indices = DataTypes.indices(schema, "Field1", "Field3");
        Assert.assertEquals(0, indices[0]);
        Assert.assertEquals(2, indices[1]);
    }
    
    @Test
    public void projectionTest() {
        final RecordDataType projectedSchema = DataTypes.projectSchema(schema, "Field1", "Field3");
        Assert.assertEquals("Field1", projectedSchema.getName(0));
        Assert.assertEquals("Field3", projectedSchema.getName(1));
        Assert.assertEquals(DoubleDataType.class, projectedSchema.getType(0).getClass());
        Assert.assertEquals(IntegerDataType.class, projectedSchema.getType(1).getClass());   
        Assert.assertEquals(2, Lists.newArrayList(projectedSchema.getMembers()).size());
    }
    
    @Test
    public void testGetFields() {
        final Record record = Record.of(schema, 1.0, 1, 2);
        final Function<Record, Object[]> function = Records.getFieldsFunction();
        final Object[] result = function.apply(record);
        Assert.assertEquals(result[1], Integer.valueOf(1));
        Assert.assertEquals(result[2], Integer.valueOf(2));
    }

}

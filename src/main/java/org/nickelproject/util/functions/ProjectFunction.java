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
package org.nickelproject.util.functions;

import java.io.Serializable;
import java.lang.reflect.Array;

import javax.annotation.Nonnull;

import com.google.common.base.Function;

public final class ProjectFunction<T> implements Function<T[], T[]>, Serializable {
    private static final long serialVersionUID = 1L;
    private final int[] fields;
    
    public ProjectFunction(final int... fields) {
        this.fields = fields;
    }

    @Override
    public T[] apply(@Nonnull final T[] pVal) {
        final T[] array = (T[]) Array.newInstance(pVal.getClass().getComponentType(), fields.length);
        for (int i = 0; i < fields.length; i++) {
            array[i] = pVal[fields[i]];
        }
        return array;
    }
}

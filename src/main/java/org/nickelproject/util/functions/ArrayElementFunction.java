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

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

public final class ArrayElementFunction<T> implements Function<T[], T>, Serializable {
    private final int index;
    
    public ArrayElementFunction(final int index) {
        Preconditions.checkArgument(index >= 0);
        this.index = index;
    }
    
    @Override
    public T apply(@Nonnull final T[] input) {
        return (T) input[index];
    }
}

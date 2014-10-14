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
package org.nickelproject.util.functions;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;

public final class ExceptionCatchingWrapper<F, T> implements Function<F, Pair<T, Exception>>, Serializable {
    private final Function<F, T> function;

    private ExceptionCatchingWrapper(final Function<F, T> function) {
        this.function = function;
    }
    
    public static <F, T> Function<F, Pair<T, Exception>> wrap(final Function<F, T> function) {
        return new ExceptionCatchingWrapper<F, T>(function);
    }
    
    @Override
    @Nullable
    public Pair<T, Exception> apply(@Nullable final F input) {
        Exception exception = null;
        T result = null;
        try {
            result = function.apply(input);
        } catch (Exception e) {
            exception = e;
        }
        return Pair.of(result, exception);
    }
}

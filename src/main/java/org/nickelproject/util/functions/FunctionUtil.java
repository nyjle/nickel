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

import javax.annotation.Nullable;

import org.nickelproject.nickel.externalReference.ExternalReference;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public final class FunctionUtil {

    private FunctionUtil() {
        // Prevents construction
    }
    
    public static <F, T> Function<ExternalReference<F>, ExternalReference<T>>
                            externalize(final Function<F, T> function) {
        return Functions.compose(new PutExternal<T>(), Functions.compose(function, new GetExternal<F>()));
    }
    
    public static <F, T> Function<F, T> constant(final T constant) {
        return new ConstantFunction<F, T>(constant);
    }
    
    private static class ConstantFunction<F, T> implements Function<F, T> {
        private final T constant;
        
        ConstantFunction(final T constant) {
            this.constant = constant;
        }
        
        @Override
        public T apply(@Nullable final F input) {
            return constant;
        }
    }
}

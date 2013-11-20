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
package org.nickelproject.nickel.mapReduce;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.google.common.base.Function;

public class FunctionCallable<F, T> implements Callable<T>, Serializable {
    private static final long serialVersionUID = 1L;
    private final Function<F, ? extends T> function;
    private final F argument;
    
    private FunctionCallable(final Function<F, ? extends T> function, final F argument) {
        this.function = function;
        this.argument = argument;
    }
    
    public static <F, T> FunctionCallable<F, T> of(final Function<F, ? extends T> function, final F argument) {
        return new FunctionCallable<F,T>(function, argument);
    }
    
    @Override
    public T call() throws Exception {
        return function.apply(argument);
    }
}

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

import java.util.Iterator;

import com.google.common.base.Function;

/**
 * A Mapper applies a {@link Function} to a sequence of data, returning an
 * {@link Iterator}. 
 */
public interface Mapper {
    
    /**
     * Applies the given {@link Function} to the input data, returning an {@link Iterator}. 
     * Results may be returned out-of-order. Returned
     * {@link Iterator}s are not guaranteed to be thread safe.
     */
     <F, T> Iterator<T> map(Iterator<F> pInputs, Function<F, T> function);
}

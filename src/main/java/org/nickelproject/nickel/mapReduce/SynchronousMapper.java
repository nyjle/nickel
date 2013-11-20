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
import com.google.common.collect.Iterators;


/**
 * A {@link Mapper} that applies a {@link Function} to each element of an {@link Iterator}
 * in order in the parent thread.
 */
final class SynchronousMapper implements Mapper {
    
    @Override
    public <F, T> Iterator<T> map(final Iterator<F> iterator, final Function<F, T> function) {
        return Iterators.transform(iterator, function);
    }
}

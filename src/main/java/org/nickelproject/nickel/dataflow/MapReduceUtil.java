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
package org.nickelproject.nickel.dataflow;

import java.io.IOException;

import org.nickelproject.nickel.mapReduce.Mapper;
import org.nickelproject.util.CloseableIterator;
import org.nickelproject.util.RethrownException;

import com.google.common.base.Function;

public final class MapReduceUtil {
    
    private MapReduceUtil() {
        // Prevents construction
    }

    public static <T, U, V> V mapReduce(
            final Source<T> source, 
            final Function<T, U> function,
            final CollectorInterface<U, V> reducer, 
            final Mapper mapper) {
        final CloseableIterator<T> iterator = source.iterator();
        final CloseableIterator<U> results = mapper.map(iterator, function);
        final Reductor<U, V> reductor = reducer.reductor();
        
        while (results.hasNext()) {
            reductor.collect(results.next());
        }
        try {
            results.close();
        } catch (IOException e) {
            throw RethrownException.rethrow(e);
        }
        return reductor.reduce();
    }
}

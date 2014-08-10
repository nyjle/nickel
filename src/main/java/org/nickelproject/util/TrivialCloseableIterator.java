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
package org.nickelproject.util;

import java.io.IOException;
import java.util.Iterator;

public final class TrivialCloseableIterator<T> implements CloseableIterator<T> {
    private final Iterator<T> iterator;

    public static <T> CloseableIterator<T> create(final Iterator<T> iterator) {
        return new TrivialCloseableIterator<T>(iterator);
    }
    
    private TrivialCloseableIterator(final Iterator<T> iterator) {
        this.iterator = iterator;
    }
    
    @Override
    public void close() throws IOException {
        // Intentionally empty
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
    
    @Override
    public T next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}

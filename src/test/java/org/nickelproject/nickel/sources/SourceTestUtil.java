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
package org.nickelproject.nickel.sources;

import java.util.Iterator;

import org.nickelproject.nickel.dataflow.Source;

public final class SourceTestUtil {
    
    private SourceTestUtil() {
        // Prevents Construction
    }
    
    public static <T> Iterable<T> toIterable(final Source<T> source) {
        return new SourceIterable<T>(source);
    }
    
    /**
     * This should be used only in tests where the source doesn't need to be closed.
     */
    private static final class SourceIterable<T> implements Iterable<T> {
        private final Source<T> source;
        
        public SourceIterable(final Source<T> source) {
            this.source = source;
        }

        @Override
        public Iterator<T> iterator() {
            return source.iterator();
        }
    }
}

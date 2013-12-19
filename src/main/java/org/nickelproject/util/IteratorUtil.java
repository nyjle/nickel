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

import java.util.Iterator;

import com.google.common.base.Preconditions;

public final class IteratorUtil {
    
    private IteratorUtil() {
        // Prevents construction
    }
    
    public static Iterator<Object[]> arrayOfIterators(final Iterator[] iterators) {
        return new ArrayOfIterators(iterators);
    }
    
    private static final class ArrayOfIterators implements Iterator<Object[]> {
        private final Iterator[] iterators;
        
        public ArrayOfIterators(final Iterator[] iterators) {
            Preconditions.checkArgument(iterators.length >= 1);
            this.iterators = iterators;
        }
        
        @Override
        public boolean hasNext() {
            return iterators[0].hasNext();
        }

        @Override
        public Object[] next() {
            final Object[] retVal = new Object[iterators.length];
            for (int i = 0; i < iterators.length; i++) {
                retVal[i] = iterators[i].next();
            }
            return retVal;
        }

        @Override
        public void remove() {
            for (int i = 0; i < iterators.length; i++) {
                iterators[i].remove();
            }
        }
    }
}

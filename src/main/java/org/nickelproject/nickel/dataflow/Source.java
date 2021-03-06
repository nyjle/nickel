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

import java.io.Serializable;

import org.nickelproject.util.CloseableIterator;


// Rather than Size could have upperBound, lowerBound
public interface Source<T> extends Serializable {
    int unknownSize = -1;
    
    CloseableIterator<T> iterator();
    
    // Size is just a guideline here.
    Source<? extends Source<T>> partition(final int sizeGuideline);
    int size();
}

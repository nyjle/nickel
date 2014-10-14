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

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * Given a pre-specified start and end index this {@link Function} returns the substring of a
 * {@link String} starting at the start index and ending 1 before the end index.
 * 
 * If the argument {@link String#length()} < start then an empty {@link String} is
 * returned. 
 * 
 * If the argument {@link String#length()} < end then the substring up to the end of the argument
 * {@link String} is returned.
 */
public final class SubString implements Function<String, String>, Serializable {
    private final int start;
    private final int end;
    
    public SubString(final int start, final int end) {
        Preconditions.checkArgument(start >= 0);
        Preconditions.checkArgument(end > start);
        this.start = start;
        this.end = end;
    }
    
    @Override
    public String apply(@Nonnull final String input) {
        final int endIndex = Math.min(end, input.length());
        return input.length() <= start ? "" : input.substring(start, endIndex);
    }
}

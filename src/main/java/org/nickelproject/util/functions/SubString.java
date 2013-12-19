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

import javax.annotation.Nonnull;

import com.google.common.base.Function;

public final class SubString implements Function<String, String> {
    private final int start;
    private final int end;
    
    public SubString(final int start, final int end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public String apply(@Nonnull final String input) {
        return input.substring(start, end);
    }
}

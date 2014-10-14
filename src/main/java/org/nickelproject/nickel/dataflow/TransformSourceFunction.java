/*
 * Copyright (c) 2013, 2014 Nigel Duffy
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

import javax.annotation.Nonnull;

import com.google.common.base.Function;

public final class TransformSourceFunction<S, T> implements Function<Source<S>, Source<T>> {
    private final Function<S, T> transform;
    
    public TransformSourceFunction(final Function<S, T> transform) {
        this.transform = transform;
    }
    
    @Override
    public Source<T> apply(@Nonnull final Source<S> input) {
        return Sources.transform(input, transform);
    }
}

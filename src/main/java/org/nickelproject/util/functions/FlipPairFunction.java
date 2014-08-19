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

import org.nickelproject.util.tuple.Pair;

import com.google.common.base.Function;

public class FlipPairFunction<A, B> implements Function<Pair<A, B>, Pair<B, A>> {

    @Override
    public Pair<B, A> apply(Pair<A, B> input) {
        return Pair.of(input.getB(), input.getA());
    }
}

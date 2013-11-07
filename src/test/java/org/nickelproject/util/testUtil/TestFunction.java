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
package org.nickelproject.util.testUtil;

import com.google.common.base.Function;
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value =
                "NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
public final class TestFunction implements Function<Double, Double> {

    @Override
    public Double apply(final Double input) {
        return input * input;
    }
}

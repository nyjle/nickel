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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.base.Function;

/**
 * A basic test to demonstrate
 * {@link TaggedSuite}.
 */
public final class Test1 {
    @Test
    public void testFunction() throws Exception {
        final double five = 5.0;
        final Function<Double, Double> function = new TestFunction();
        final Number vResult = function.apply(Double.valueOf(five));
        assertEquals(Double.valueOf(five * five), vResult);
    }
}

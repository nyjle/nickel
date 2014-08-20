/*
 * Copyright (c) 2014 Nigel Duffy
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

import java.util.Random;

public final class RandomUtil {
    
    private RandomUtil() {
        // Prevents construction
    }
    
    public static double[] randomDoubleArray(final int mDimensionality) {
        final Random random = new Random();
        final double[] retVal = new double[mDimensionality];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = random.nextDouble();
        }
        return retVal;
    }
}

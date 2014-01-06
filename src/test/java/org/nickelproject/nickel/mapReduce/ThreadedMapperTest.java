/*
 * Copyright (c) 2013 Numerate, Inc
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
package org.nickelproject.nickel.mapReduce;

import org.nickelproject.suites.UnitAnnotation;


/**
 * Tests SynchronousMapper.
 */
@UnitAnnotation
public final class ThreadedMapperTest extends BaseMapperTest {
    
    @Override
    public void testBasicFunctionality() {
        testBasicFunctionality(new ThreadedMapper(Runtime.getRuntime().availableProcessors()));
    }

    @Override
    public void testThroughput() throws Exception {
        final double overheadFactor = 0.5;
        final double overhead = 100;
        final int numBig = 10;
        final int bigSize = 1000;
        final int numSmall = 10000;
        final int smallSize = 1;
        final int safeNumberOfThreads = 8;
        // Assume we're ok up to 8x as we're not doing any real work
        for (int i = 1; i <= Runtime.getRuntime().availableProcessors() * safeNumberOfThreads; i *= 2) {
            getLogger().info("Testing threads " + i);
            testMapper(new ThreadedMapper(i), numBig, 0, bigSize, 0,
                    overhead + (smallSize + overheadFactor) * numSmall); 
            testMapper(new ThreadedMapper(i), numSmall, 0, smallSize, 0,
                    overhead + (smallSize + overheadFactor) * numSmall); 
        }
    }
}

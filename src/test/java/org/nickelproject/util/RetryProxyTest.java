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

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.util.testUtil.UnitAnnotation;

@UnitAnnotation
public final class RetryProxyTest {

    @Test(expected = Exception.class)
    public void testAlwaysThrowException() throws Exception {
        final DumbInterface dumb = RetryProxy.newInstance(DumbInterface.class, new DumbClass());
        dumb.alwaysThrowException();
    }

    @Test(expected = Error.class)
    public void testAlwaysThrowError() {
        final DumbInterface dumb = RetryProxy.newInstance(DumbInterface.class, new DumbClass());
        dumb.alwaysThrowError();
    }

    @Test
    public void testAlwaysSucceed() {
        final DumbInterface dumb = RetryProxy.newInstance(DumbInterface.class, new DumbClass());
        Assert.assertTrue(dumb.alwaysSucceed());
    }

    @Test
    public void test2Throws() throws Exception {
        final DumbInterface dumb = RetryProxy.newInstance(DumbInterface.class, new DumbClass());
        Assert.assertTrue(dumb.throwNTimes(2));
    }

    @Test(expected = Exception.class)
    public void test4Throws() throws Exception {
        final int n = 4;
        final DumbInterface dumb = RetryProxy.newInstance(DumbInterface.class, new DumbClass());
        Assert.assertTrue(dumb.throwNTimes(n));
    }

    interface DumbInterface {
        Boolean alwaysThrowException() throws Exception;
        Boolean alwaysSucceed();
        Boolean alwaysThrowError();
        Boolean throwNTimes(int n) throws Exception;
    }

    private static final class DumbClass implements DumbInterface {
        private int failcount = 0;

        @Override
        public Boolean alwaysThrowException() throws Exception {
            throw new Exception("I always throw an exception");
        }

        @Override
        public Boolean alwaysSucceed() {
            return true;
        }

        @Override
        public Boolean alwaysThrowError() {
            throw new Error("I always throw an Error");
        }

        @Override
        public Boolean throwNTimes(final int n) throws Exception {
            if (failcount++ < n) {
                throw new Exception("I throw " + n + " times");
            }
            return true;
        }
    }
}

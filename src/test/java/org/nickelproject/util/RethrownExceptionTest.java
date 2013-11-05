/*
 * Copyright (c) 2013 Numerate, Inc
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.nickelproject.util.RethrownException;

/**
 * Unit tests for {@link RethrownException}.
 */
public final class RethrownExceptionTest {
    /*
     * Note that NONE of these methods have a "throws" clause. They never
     * should! We want to verify that the none of the rethrowing code throws
     * checked exceptions of any kind.
     */

    @Test
    public void testThrowable() {
        testCheckedThrowable(new MyThrowable());
    }

    /**
     * There are no JVM classes that extend 'Throwable' and aren't 'Exception'
     * or 'RuntimeException', so we have to create our own.
     */
    private static class MyThrowable extends Throwable {
        private static final long serialVersionUID = 1L;

        public MyThrowable() {
            return;
        }
    }

    @Test
    public void testError() {
        testUncheckedThrowable(new InternalError(null));
    }

    @Test
    public void testException() {
        testCheckedThrowable(new IOException());
    }

    @Test
    public void testRuntimeException() {
        testUncheckedThrowable(new ArrayIndexOutOfBoundsException());
    }

    @Test
    public void testInterruptedException() {
        // If the thread is interrupted, then everything should work just fine.
        Thread.currentThread().interrupt();
        assertTrue(Thread.currentThread().isInterrupted());
        testCheckedThrowable(new InterruptedException());
        // Clear the interrupted flag...
        assertTrue(Thread.interrupted());
        assertFalse(Thread.currentThread().isInterrupted());

        // If the thread is not interrupted, then we should get a different
        // exception.
        final InterruptedException vInterruptedException = new InterruptedException();
        try {
            RethrownException.rethrow(vInterruptedException);
            fail();
        } catch (final IllegalStateException e) {
            assertSame(vInterruptedException, e.getCause());
        }
        // Make sure we didn't screw up the interrupted flag doing our magic
        // inside "rethrow".
        assertFalse(Thread.currentThread().isInterrupted());
    }

    private void testCheckedThrowable(final Throwable pThrowable) {
        assertFalse(pThrowable instanceof RuntimeException);
        assertFalse(pThrowable instanceof Error);
        try {
            RethrownException.rethrow(pThrowable);
            fail();
        } catch (final RethrownException e) {
            assertSame(pThrowable, e.getCause());
        }
    }

    private void testUncheckedThrowable(final Throwable pThrowable) {
        assertTrue(pThrowable instanceof RuntimeException || pThrowable instanceof Error);
        try {
            RethrownException.rethrow(pThrowable);
            fail();
        } catch (final RuntimeException e) {
            assertSame(pThrowable, e);
        } catch (final Error e) {
            assertSame(pThrowable, e);
        }
    }
}

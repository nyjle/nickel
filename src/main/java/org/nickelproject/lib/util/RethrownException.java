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
package org.nickelproject.lib.util;

/**
 * An exception adapter which rethrows checked exceptions/throwables as runtime
 * exceptions. In general, checked exceptions indicate error cases which may be
 * recoverable. They should be rethrown using this class in cases where they
 * have been deemed unrecoverable.
 * <p>
 * All construction and rethrowing must go through the
 * {@link #rethrow(Throwable)} static method. This method will correctly rethrow
 * runtime exceptions OR wrap and rethrow checked exceptions.
 * <p>
 * Note that the {@link #rethrow(Throwable)} method "returns" an {@link Error}.
 * However, it will never actually return anything. It will always throw some
 * type of unchecked exception. This allows for the following idiom:
 *
 * <pre>
 * try {
 *     foobarWhichThrowsVariousCheckedExceptions();
 * } catch (Exception e) {
 *     throw RethrownException.rethrow(e);
 * }
 * </pre>
 *
 * Without the return value we would have to eliminate the word "throws", and
 * then certain compiler checks would fail.
 */
public final class RethrownException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Rethrows the given {@link Throwable} to the best of its ability. If the
     * {@link Throwable} is an unchecked exception, it will be rethrown
     * directly. Otherwise, it is wrapped in an instance of this class and then
     * rethrown.
     *
     * @throws IllegalStateException
     *             if pThrowable is an InterrupedException and the current
     *             thread is not interrupted.
     * @throws Error
     *             if pThrowable is an error.
     * @throws RuntimeException
     *             if pThrowable is a runtime exception.
     * @throws RethrownException
     *             otherwise.
     * @return this method will never return the {@link Error} that it claims.
     */
    public static Error rethrow(final Throwable pThrowable) {
        try {
            throw pThrowable;
        // The only two types of unchecked exceptions are Error and
        // RuntimeException.
        } catch (final Error e) {
            throw e;
        } catch (final RuntimeException e) {
            throw e;
        } catch (final InterruptedException e) {
            // See here for details:
            // http://www-128.ibm.com/developerworks/java/library/j-jtp05236.html
            if (!Thread.currentThread().isInterrupted()) {
                // The caller is asking us to rethrow an InterruptedException,
                // but if we do so the interrupted state of the thread will be
                // lost up the call chain. We don't want to lose this state, so
                // we force callers to reset the interrupted status before
                // calling here.
                throw new IllegalStateException("Thread must be interrupted before rethrowing.", e);
            } else {
                throw new RethrownException(e);
            }
        } catch (final Throwable e) {
            throw new RethrownException(e);
        }
    }

    private RethrownException(final Throwable pCause) {
        super(pCause);
        if (pCause instanceof Error || pCause instanceof RuntimeException) {
            throw new IllegalArgumentException("Why are you rethrowing an unchecked exception?");
        }
    }
}

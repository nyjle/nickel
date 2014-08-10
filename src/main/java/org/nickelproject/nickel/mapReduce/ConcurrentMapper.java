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

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.nickelproject.util.CloseableIterator;
import org.nickelproject.util.RethrownException;

import com.google.common.base.Function;

/**
 * Base-class for Mapper implementations that execute work via submission to a
 * {@link CompletionService}.
 * <p>
 * Execution is throttled so that a maximum number of operations are
 * in-flight at any time. Sub-classes are responsible for providing this dynamic
 * maximum (@see {@link #getMaxOutstanding()}.
 */
public abstract class ConcurrentMapper implements Mapper {
    
    /**
     * Returns the maximum permitted number of outstanding tasks submitted to
     * this Mapper's CompletionService {@link #newCompletionService()}.
     * This number includes those tasks queued for execution, tasks that are
     * executing, and completed tasks yet to have their result inspected.
     */
    protected abstract int getMaxOutstanding();
    
    /**
     * Returns a new {@link CompletionService} that this Mapper will use for
     * asynchronous task execution.
     * <p>
     * This method is called by the Mapper for each call to {@link #map}, and
     * the returned CompletionService is used only for executing tasks for that
     * particular map operation. Hence, the returned CompletionService must not
     * be shared between calls to this method, or reused in any way. (However,
     * the CompletionService may be backed by some shared execution resource,
     * such as an {@link java.util.concurrent.ExecutorService}.
     * <p>
     * A {@link java.util.concurrent.RejectedExecutionException} thrown by the
     * CompletionService will not be recoverable by the Mapper. Hence the
     * CompletionService should not throw these due to simple capacity
     * restrictions (e.g. if the CompletionService is backed by a
     * {@link java.util.concurrent.ThreadPoolExecutor}, then the
     * ThreadPoolExecutor should not use a bounded BlockingQueue for tasks).
     */
    protected abstract <T> CompletionService<T> newCompletionService();
    
    protected abstract int getTimeOut();
    
    // TODO Not thread safe
    @Override
    public final <F, T> CloseableIterator<T> map(final Iterator<F> inputs, final Function<F, T> function) {
        return new CloseableIterator<T>() {
            private int outstandingCount = 0;
            private final CompletionService<T> completionService = newCompletionService();
            
            @Override
            public boolean hasNext() {
                return inputs.hasNext() || outstandingCount > 0;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                
                while (true) { // polling loop on mCompletionService 
                    while (inputs.hasNext() && outstandingCount < getMaxOutstanding()) {
                        final F input = inputs.next();
                        final Callable<T> vCallable = FunctionCallable.of(function, input);
                        completionService.submit(vCallable); 
                        outstandingCount++;
                    }
                    
                    // The poll timeout determines how quickly we can adjust to dynamic cluster sizes,
                    // i.e. so that we can loop and submit more work if getMaxOutstanding() allows.
                    Future<T> vFuture;
                    try {
                        vFuture = completionService.poll(getTimeOut(), TimeUnit.MILLISECONDS);
                    } catch (final InterruptedException e) {
                        throw RethrownException.rethrow(e);
                    }
                    if (vFuture != null) {
                        outstandingCount--;
                        try {
                            return vFuture.get();
                        } catch (final Exception e) {
                            throw RethrownException.rethrow(e);
                        }
                    }
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void close() throws IOException {
                // Nothing to be done here, if we weren't using a competionservice we could perhaps
                // cancel all outstanding jobs.
            }
        };
    }
}

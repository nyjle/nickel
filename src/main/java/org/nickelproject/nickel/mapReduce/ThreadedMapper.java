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
package org.nickelproject.nickel.mapReduce;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * A {@link Mapper} that executes map operations concurrently via a thread-pool.
 * <p>
 * The thread pool is created on {@link ThreadedMapper} construction, and
 * shutdown at {@link ThreadedMapper} finalization.
 */
public final class ThreadedMapper extends ConcurrentMapper {
    private static final int timeOut = 100;
    private final ExecutorService mExecutor;
    private final int mMaxNumOutstanding;

    /**
     * Creates a new ThreadedMapper backed by a thread-pool of the given size.
     * 
     * @param pNumThreads the (fixed) size of the thread-pool
     */
    public ThreadedMapper(@Named("NumMapperThreads") final int pNumThreads) {
        this(pNumThreads, pNumThreads, new ThreadFactoryBuilder().setDaemon(true).build());
    }
    
    /**
     * Creates a new ThreadedMapper backed by a thread-pool of the given size.
     * 
     * @param pNumThreads the (fixed) size of the thread-pool
     * @param threadGroupPrefix the prefix of the thread name for the threads used by this mapper
     */
    @Inject
    public ThreadedMapper(@Named("NumMapperThreads") final int pNumThreads,
            @Named("MaxOutstandingTasks") final int maxOutstanding,
            @Named("ThreadNamePrefix") final String threadGroupPrefix) {
        this(pNumThreads, maxOutstanding, new BasicThreadFactory.Builder()
                                                    .namingPattern(threadGroupPrefix + "-%d")
                                                    .daemon(true)
                                                    .build());
    }
    
    /**
     * Creates a new ThreadedMapper backed by a thread-pool of the given size.
     * 
     * @param pNumThreads the (fixed) size of the thread-pool
     * @param pThreadFactory the ThreadFactory with which to create the
     *        thread-pool's threads.
     */
    public ThreadedMapper(final int pNumThreads, final int maxOutstanding,
            final ThreadFactory pThreadFactory) {
        mMaxNumOutstanding = Math.max(pNumThreads, maxOutstanding);
        mExecutor = Executors.newFixedThreadPool(pNumThreads, pThreadFactory);
    }

    @Override
    protected int getMaxOutstanding() {
        return mMaxNumOutstanding;
    }

    @Override
    protected int getTimeOut() {
        return timeOut;
    }
    
    @Override
    protected <T> CompletionService<T> newCompletionService() {
        return new ExecutorCompletionService<T>(mExecutor);
    }
}

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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.Reflection;

// TODO Need to add a random delay
public final class RetryProxy<T> implements InvocationHandler {
    private static final long initialDelay = 1;
    private static final long increment    = 2;
    private static final long maxRetries   = 3;
    private static final int  secondsToMillis = 1000;
    private final Logger logger;
    private final T           obj;

    public static <T> T newInstance(final Class<T> pClass, final T obj) {
        return Reflection.newProxy(pClass, new RetryProxy<T>(obj, LoggerFactory.getLogger(pClass)));
    }

    private RetryProxy(final T obj, final Logger logger) {
        this.obj = obj;
        this.logger = logger;
    }

    @Override
    public Object invoke(final Object arg0, final Method arg1, final Object[] arg2) throws Throwable {
        int retryCount = 0;
        while (true) {
            try {
                return arg1.invoke(obj, arg2);
            } catch (final InvocationTargetException e) {
                if (retryCount >= maxRetries) {
                    throw RethrownException.rethrow(e.getCause());
                } else {
                    logger.info("Retrying after exception", e.getCause());
                    Thread.sleep((initialDelay + retryCount * increment) * secondsToMillis);
                }
            }
            retryCount++;
        }
    }
}

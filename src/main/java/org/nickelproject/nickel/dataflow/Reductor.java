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
package org.nickelproject.nickel.dataflow;

/**
 * A {@link Reductor} processes a sequence of values submitted via the
 * {@link #collect} method and returns a value upon calling {@link #reduce}.
 * <p>
 * A {@link Reductor} is analogous to an {@link java.util.Iterator}, whereas
 * {@link Reducer} is analogous to {@link java.lang.Iterable}.
 *
 * @param <T>
 *            The type of the values to be reduced
 * @param <U>
 *            The type of the reduced result
 * @see Reducer
 */
public interface Reductor<T, U> {
    /**
     * Collects the given value.
     */
    void collect(T pVal);

    /**
     * Return the value of this {@link Reductor} given the input seen thus far.
     * <p>
     * If this method is called again after {@link #collect(Object)}ing more
     * input data, the behavior is undefined: it may return the aggregate over
     * all data already seen, or just for data seen since the last call to this
     * method, or it may throw an exception. Current Numatix framework code will
     * not call this method on a {@link Reductor} instance more than once.
     */
    U reduce();
}

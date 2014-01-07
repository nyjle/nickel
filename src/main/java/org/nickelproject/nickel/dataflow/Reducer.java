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

import java.io.Serializable;

/**
 * Conceptually, a {@link Reducer} collects and processes a sequence of values,
 * and returns a value when requested to 'reduce' the values it has seen so far.
 * <p>
 * Like a {@link Source}, a {@link Reducer} is intended to be re-usable,
 * therefore it is actually a wrapper for a {@link Reductor}, instances of which
 * are the stateful component of a reduction operation. Works by analogy with
 * {@link java.lang.Iterable}/{@link java.util.Iterator}.
 * <p>
 * The Reducer Contract:
 * <ul>
 * <li>Type: A Reducer must be declared as a top-level or static-nested class
 * that implements com.numerati.numatix.core.component.Reducer.
 * <li>Purity: Reducing the same set of values collected in the same order must
 * always yield the same return value or a semantically acceptable value.
 * <li>Side Effects: Collecting a value or reducing collected values must not
 * cause any side effects. Side effects are semantically observable changes in
 * program or external state. 'Observable' can mean different things in
 * different contexts, but as a general rule a Reducer may not:
 * <ul>
 * <li>Write to external storage.
 * <li>Write to the network.
 * <li>Mutate a static field.
 * <li>Mutate collected objects.
 * </ul>
 * </ul>
 *
 * @param <T>
 *            The type of the values to be reduced
 * @param <U>
 *            The type of the reduced result
 */
public interface Reducer<T, U> extends Serializable {
    /**
     * Returns a {@link Reductor} for performing the reduction operation.
     */
    Reductor<T, U> reductor();
}

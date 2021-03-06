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
package org.nickelproject.util.tuple;

import java.io.Serializable;

public final class Pair<A, B> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final A a;
    private final B b;

    private Pair(final A a, final B b) {
        this.a = a;
        this.b = b;
    }

    public static <A, B> Pair<A, B> of(final A a, final B b) {
        return new Pair<A, B>(a, b);
    }

    public A getA() {
        return a;
    }

    public  B getB() {
        return b;
    }
    
    @Override
    public String toString() {
        return "(" + a.toString() + "," + b.toString() + ")";
    }
}

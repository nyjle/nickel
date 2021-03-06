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
package org.nickelproject.nickel.externalReference;

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.nickel.TestModule;
import org.nickelproject.util.testUtil.UnitAnnotation;

import com.google.inject.Guice;

@UnitAnnotation
public final class ExternalReferenceTest {

    @Test
    public void testGet() {
        Guice.createInjector(new TestModule());
        final String testString = "This is a test string.";
        final ExternalReference<String> reference = ExternalReference.of(testString);
        Assert.assertEquals(testString, reference.get());
    }
}

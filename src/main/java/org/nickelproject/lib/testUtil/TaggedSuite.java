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
package org.nickelproject.lib.testUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A test suite that is generated from classes tagged with a specified interface
 * or annotation.
 * <p>
 * Test classes may be added to the suite in one of three ways: 1) by extending
 * an interface then listing that interface in the {@link InterfaceTags}
 * annotation in this test suite; 2) by applying an interface then listing that
 * interface in the {@link AnnotationTags} annotation; and 3) by explicitly
 * including the class in the {@code SuiteClasses} annotation. For example, the
 * following test suite will run tests from classes that have the
 * {@code Example1} annotation or the {@code Example2} interface and the
 * {@code Example3} class:
 *
 * <pre>
 * &#064;RunWith(TaggedSuite.class)
 * &#064;TaggedSuite.AnnotationTags(Example1.class)
 * &#064;TaggedSuite.InterfaceTags(Example2.class)
 * &#064;Suite.SuiteClasses(Example3.class)
 * public class ExampleTestSuite {
 * }
 *
 * &#064;Example1
 * public class ExampleTest1 {
 * }
 *
 * public class ExampleTest2 implements Example2 {
 * }
 *
 * public class ExampleTest3 {
 * }
 * </pre>
 */
public class TaggedSuite extends Suite {
    private static final Logger kLogger = LoggerFactory.getLogger(TaggedSuite.class);

    /**
     * An annotation used to list the classes used to tag tests.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface InterfaceTags {
        Class<?>[] value();
    }

    /**
     * An annotation used to list the annotations used to tag tests.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface AnnotationTags {
        Class<?>[] value();
    }

    public TaggedSuite(final Class<?> klass, final RunnerBuilder builder) throws InitializationError {
        super(klass, getRunners(getTaggedClasses(klass), builder));
    }

    public static List<Runner> getRunners(final Class<?>[] classes, final RunnerBuilder builder)
            throws InitializationError {
        final List<Runner> runners = new LinkedList<Runner>();

        for (final Class<?> klazz : classes) {
            if (klazz.isAnnotationPresent(RunWith.class)
                    && klazz.getAnnotation(RunWith.class).value().equals(Parameterized.class)) {
                // We cannot support chained runners
                runners.add(builder.safeRunnerForClass(klazz));
                kLogger.warn("Cannot chain LoggingRunner with Parameterized runner. "
                        + "You must add the LoggingTestWatcher manually to class {}", klazz.getName());
            } else {
                runners.add(new LoggingRunner(klazz));
            }
        }
        return runners;
    }

    // Get the classes that make up this test suite
    private static Class<?>[] getTaggedClasses(final Class<?> klass) throws InitializationError {
        final List<String> vClassNames = Lists.newArrayList(Iterables.concat(
                ClasspathUtil.getAllConcreteSubClasses(getInterfaceTags(klass)),
                ClasspathUtil.getAnnotatedClasses(getAnnotationTags(klass))));
        final Class<?>[] vSuiteClasses = getSuiteClasses(klass);
        final Class<?>[] vRetVal = new Class<?>[vClassNames.size() + vSuiteClasses.length];
        for (int i = 0; i < vClassNames.size(); i++) {
            try {
                vRetVal[i] = Class.forName(vClassNames.get(i));
            } catch (final ClassNotFoundException e) {
                throw new InitializationError(e);
            }
        }
        int j = 0;
        for (int i = vClassNames.size(); i < vRetVal.length; i++) {
            vRetVal[i] = vSuiteClasses[j++];
        }
        return vRetVal;
    }

    /**
     * Gets the values from the {@code InterfaceTags} annotation, i.e., the
     * interfaces used to tag this test suite.
     */
    private static Class<?>[] getInterfaceTags(final Class<?> klass) throws InitializationError {
        final InterfaceTags annotation = klass.getAnnotation(InterfaceTags.class);
        return annotation == null ? new Class<?>[0] : annotation.value();
    }

    /**
     * Gets the values from the {@code AnnotationTags} annotation, i.e., the
     * annotations used to tag this test suite.
     */
    private static Class<?>[] getAnnotationTags(final Class<?> klass) throws InitializationError {
        final AnnotationTags annotation = klass.getAnnotation(AnnotationTags.class);
        return annotation == null ? new Class<?>[0] : annotation.value();
    }

    /**
     * Gets the values from {@code SuiteClasses}, i.e., maintains the interface
     * of the parent class.
     */
    private static Class<?>[] getSuiteClasses(final Class<?> klass) {
        final SuiteClasses vAnnotation = klass.getAnnotation(SuiteClasses.class);
        return vAnnotation == null ? new Class<?>[0] : vAnnotation.value();
    }
}

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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javassist.ClassPool;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.nickelproject.lib.util.RethrownException;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * A static utility class for getting resources from the class path.
 */
public final class ClasspathUtil {
    // The eclipse junit plugin appears to add to the classpath.
    // Unfortunately the classes contained on those paths are not accessible to
    // javassist
    // Therefore we filter them out.
    private static final String kEclipseFilter = "configuration/org.eclipse.osgi/bundles";

    private ClasspathUtil() {
        // Prevents construction.
    }

    /**
     * Find classes annotated with one or more of the arguments.
     *
     * @param pAnnotations
     *            The annotations to look for
     * @return An iterable of class names
     */
    public static Iterable<String> getAnnotatedClasses(final Class<?>... pAnnotations) {
        final String[] vAnnotationNames = new String[pAnnotations.length];
        for (int i = 0; i < vAnnotationNames.length; i++) {
            vAnnotationNames[i] = pAnnotations[i].getName();
        }
        final Iterable<String> vAllClasses = Iterables.transform(getResourcesOnClassPath("class"),
                new Function<String, String>() {
                    @Override
                    public String apply(final String pInput) {
                        return FilenameUtils.getBaseName(pInput);
                    }
                });
        return Iterables.filter(vAllClasses, Predicates.and(getIsConcrete(), new HasAnnotation(vAnnotationNames)));
    }

    /**
     * Gets all of the non-abstract, non-interface subclasses of the argument
     * classes. Useful for tagging interfaces.
     *
     * @param pTags
     *            An array of classes whose concrete descendants will be
     *            searched for
     * @return An iterable of class names
     */
    public static Iterable<String> getAllConcreteSubClasses(final Class<?>... pTags) {
        buildClassDag();
        final Iterable<String> vResults = getAllSubClasses(pTags);

        // Filters the results to ensure that the files are not abstract or interfaces.
        return Iterables.filter(vResults, getIsConcrete());
    }

    /**
     * Gets all of the resources on the class path with the provided extension.
     *
     * @param pExtension
     *            The extension string to look for
     * @return An iterable of strings containing the full resource name (i.e.,
     *         including extension)
     */
    public static Iterable<String> getResourcesOnClassPath(final String pExtension) {
        return Iterables.filter(getResourcesOnClassPath(), getHasExtension(pExtension));
    }

    /**
     * Gets all of the resources on the class path.
     *
     * @return An iterable of the names of all resources on the classpath
     */
    public static Iterable<String> getResourcesOnClassPath() {
        if (kAllResources == null) {
            kAllResources = Iterables.concat(
            // Get all the resources from files
                    Iterables.concat(Iterables.transform(
                            Iterables.filter(getClassPath(), Predicates.not(getHasExtension("jar"))),
                            new GetFileFunction())),
                    // Get all the resources from JARs
                    Iterables.concat(Iterables.transform(Iterables.filter(getClassPath(), getHasExtension("jar")),
                            new GetJarEntries())));
        }
        return kAllResources;
    }

    private static Iterable<String>         kAllResources = null;

    // This is the Class DAG
    private static Multimap<String, String> kParents      = null;
    private static Multimap<String, String> kChildren     = null;

    // Builds the inheritance DAG
    private static void buildClassDag() {
        if (kParents == null) {
            kParents = HashMultimap.create();
            kChildren = HashMultimap.create();
            for (final String vFullClassName : getResourcesOnClassPath("class")) {
                final String vClassName = FilenameUtils.getBaseName(vFullClassName);
                final ClassFile vClassFile = getClassFile(vClassName);
                final String vSuperClass = vClassFile.getSuperclass();
                kParents.put(vClassName, vSuperClass);
                kChildren.put(vSuperClass, vClassName);
                for (final String vInterface : vClassFile.getInterfaces()) {
                    kParents.put(vClassName, vInterface);
                    kChildren.put(vInterface, vClassName);
                }
            }
        }
    }

    // Performs a breadth first search of the inheritance DAG to find subclasses
    private static Iterable<String> getAllSubClasses(final Class<?>... pTags) {
        final Deque<String> vClassNames = Lists.newLinkedList();
        final Set<String> vResults = Sets.newHashSet();
        for (final Class<?> vClass : pTags) {
            vClassNames.add(vClass.getCanonicalName());
        }
        while (!vClassNames.isEmpty()) {
            final String vCurrentClass = vClassNames.pollFirst();
            for (final String vChild : kChildren.get(vCurrentClass)) {
                vClassNames.addLast(vChild);
                vResults.add(vChild);
            }
        }
        return vResults;
    }

    private static final class GetJarEntries implements Function<String, Iterable<String>> {
        @Override
        @edu.umd.cs.findbugs.annotations.SuppressWarnings(value =
                    "NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
        public Iterable<String> apply(final String input) {
            try {
                final JarFile vJarFile = new JarFile(input);
                final List<JarEntry> jarFileEntries = Collections.list(vJarFile.entries());
                vJarFile.close();
                return Iterables.transform(jarFileEntries, new Function<JarEntry, String>() {
                    @Override
                    public String apply(final JarEntry pInput2) {
                        return getResourceName(pInput2);
                    }
                });
            } catch (final IOException e) {
                throw RethrownException.rethrow(e);
            }
        }
    }

    private static final class GetFileFunction implements Function<String, Iterable<String>> {
        @Override
        @edu.umd.cs.findbugs.annotations.SuppressWarnings(value =
                    "NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
        public Iterable<String> apply(final String pName) {
            final File directory = new File(pName);
            return directory.isDirectory()
                    ? Iterables.transform(FileUtils.listFiles(new File(pName), null, true),
                            new Function<File, String>() {
                            @Override
                            public String apply(final File input) {
                                return getResourceName(input, pName);
                            }
                        })
                    : Collections.<String>emptyList();
        }
    }

    private static Iterable<String> getClassPath() {
        // Removes some paths that eclipse adds during testing.
        return Iterables.filter(Lists.newArrayList(System.getProperty("java.class.path").split(File.pathSeparator)),
                Predicates.not(Predicates.containsPattern(Pattern.quote(kEclipseFilter))));
    }

    // Note that the ClassPool maintains ClassFiles obtained previously.
    private static ClassFile getClassFile(final String pResourceName) {
        try {
            return ClassPool.getDefault().get(pResourceName).getClassFile();
        } catch (final NotFoundException e) {
            throw RethrownException.rethrow(e);
        }
    }

    private static Predicate<String> getIsConcrete() {
        return new Predicate<String>() {
            @Override
            public boolean apply(final String pName) {
                return !getClassFile(pName).isAbstract() && !getClassFile(pName).isInterface();
            }
        };
    }

    private static Predicate<String> getHasExtension(final String pExtension) {
        return new Predicate<String>() {
            @Override
            public boolean apply(final String pName) {
                return FilenameUtils.isExtension(pName, pExtension);
            }
        };
    }

    private static final class HasAnnotation implements Predicate<String> {
        private final String[] mAnnotations;

        public HasAnnotation(final String... pAnnotations) {
            mAnnotations = pAnnotations;
        }

        @Override
        public boolean apply(final String pName) {
            final AttributeInfo vInfo = getClassFile(pName).getAttribute("RuntimeVisibleAnnotations");
            boolean vRetVal = false;
            if (vInfo != null) {
                for (final Annotation vAnnotation : ((AnnotationsAttribute) vInfo).getAnnotations()) {
                    for (final String vAnnotationQuery : mAnnotations) {
                        vRetVal |= vAnnotation.getTypeName().equals(vAnnotationQuery);
                    }
                }
            }
            return vRetVal;
        }
    }

    private static String getResourceName(final File pFile, final String pRoot) {
        final String vName = pFile.getAbsolutePath().substring(pRoot.length() + 1);
        return vName.replaceAll(Pattern.quote(File.separator), ".");
    }

    private static String getResourceName(final JarEntry pJarEntry) {
        return pJarEntry.getName().replaceAll("/", ".");
    }
}

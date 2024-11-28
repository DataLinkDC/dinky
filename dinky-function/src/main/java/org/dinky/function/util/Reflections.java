/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.function.util;

import static java.lang.String.format;
import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javassist.bytecode.ClassFile;

import javax.annotation.Nullable;

import org.reflections.Configuration;
import org.reflections.ReflectionUtils;
import org.reflections.ReflectionsException;
import org.reflections.Store;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.Scanners;
import org.reflections.util.NameHelper;
import org.reflections.util.QueryFunction;
import org.reflections.vfs.Vfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reflections one-stop-shop object
 */
public class Reflections implements NameHelper {
    public static final Logger log = LoggerFactory.getLogger(Reflections.class);

    protected final transient Configuration configuration;
    protected final Store store;

    /**
     * constructs Reflections instance and scan according to the given {@link Configuration}
     * <p>it is preferred to use {@link org.reflections.util.ConfigurationBuilder} <pre>{@code new Reflections(new ConfigurationBuilder()...)}</pre>
     */
    public Reflections(Configuration configuration) {
        this.configuration = configuration;
        Map<String, Map<String, Set<String>>> storeMap = scan();
        if (configuration.shouldExpandSuperTypes()) {
            expandSuperTypes(storeMap.get(SubTypes.index()), storeMap.get(TypesAnnotated.index()));
        }
        store = new Store(storeMap);
    }

    protected Map<String, Map<String, Set<String>>> scan() {
        long start = System.currentTimeMillis();
        Map<String, Set<Map.Entry<String, String>>> collect = configuration.getScanners().stream()
                .map(Scanner::index)
                .distinct()
                .collect(Collectors.toMap(s -> s, s -> Collections.synchronizedSet(new HashSet<>())));
        Set<URL> urls = configuration.getUrls();

        (configuration.isParallel() ? urls.stream().parallel() : urls.stream()).forEach(url -> {
            Vfs.Dir dir = null;
            try {
                dir = Vfs.fromURL(url);
                for (Vfs.File file : dir.getFiles()) {
                    if (doFilter(file, configuration.getInputsFilter())) {
                        ClassFile classFile = null;
                        for (Scanner scanner : configuration.getScanners()) {
                            try {
                                if (doFilter(file, scanner::acceptsInput)) {
                                    List<Map.Entry<String, String>> entries = scanner.scan(file);
                                    if (entries == null) {
                                        if (classFile == null) classFile = getClassFile(file);
                                        entries = scanner.scan(classFile);
                                    }
                                    if (entries != null)
                                        collect.get(scanner.index()).addAll(entries);
                                }
                            } catch (Exception e) {
                                if (log != null)
                                    log.trace(
                                            "could not scan file {} with scanner {}",
                                            file.getRelativePath(),
                                            scanner.getClass().getSimpleName(),
                                            e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // nothing to do.
            } finally {
                if (dir != null) dir.close();
            }
        });

        // merge
        Map<String, Map<String, Set<String>>> storeMap = collect.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .filter(e -> e.getKey() != null)
                        .collect(Collectors.groupingBy(
                                Map.Entry::getKey,
                                HashMap::new,
                                Collectors.mapping(Map.Entry::getValue, Collectors.toSet())))));
        if (log != null) {
            int keys = 0, values = 0;
            for (Map<String, Set<String>> map : storeMap.values()) {
                keys += map.size();
                values += map.values().stream().mapToLong(Set::size).sum();
            }
            log.info(format(
                    "Reflections took %d ms to scan %d urls, producing %d keys and %d values",
                    System.currentTimeMillis() - start, urls.size(), keys, values));
        }
        return storeMap;
    }

    private boolean doFilter(Vfs.File file, @Nullable Predicate<String> predicate) {
        String path = file.getRelativePath();
        String fqn = path.replace('/', '.');
        return predicate == null || predicate.test(path) || predicate.test(fqn);
    }

    private ClassFile getClassFile(Vfs.File file) {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(file.openInputStream()))) {
            return new ClassFile(dis);
        } catch (Exception e) {
            throw new ReflectionsException("could not create class object from file " + file.getRelativePath(), e);
        }
    }

    /**
     * expand super types after scanning, for super types that were not scanned.
     * <br>this is helpful for finding the transitive closure without scanning all 3rd party dependencies.
     * <p></p>
     * for example, for classes A,B,C where A supertype of B, B supertype of C (A -> B -> C):
     * <ul>
     *     <li>if scanning C resulted in B (B->C in store), but A was not scanned (although A is a supertype of B) - then getSubTypes(A) will not return C</li>
     *     <li>if expanding supertypes, B will be expanded with A (A->B in store) - then getSubTypes(A) will return C</li>
     * </ul>
     */
    public void expandSuperTypes(Map<String, Set<String>> subTypesStore, Map<String, Set<String>> typesAnnotatedStore) {
        if (subTypesStore == null || subTypesStore.isEmpty()) return;
        Set<String> keys = new LinkedHashSet<>(subTypesStore.keySet());
        keys.removeAll(
                subTypesStore.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));
        keys.remove("java.lang.Object");
        for (String key : keys) {
            Class<?> type = forClass(key, loaders());
            if (type != null) {
                expandSupertypes(subTypesStore, typesAnnotatedStore, key, type);
            }
        }
    }

    private void expandSupertypes(
            Map<String, Set<String>> subTypesStore,
            Map<String, Set<String>> typesAnnotatedStore,
            String key,
            Class<?> type) {
        Set<Annotation> typeAnnotations = ReflectionUtils.getAnnotations(type);
        if (typesAnnotatedStore != null && !typeAnnotations.isEmpty()) {
            String typeName = type.getName();
            for (Annotation typeAnnotation : typeAnnotations) {
                String annotationName = typeAnnotation.annotationType().getName();
                typesAnnotatedStore
                        .computeIfAbsent(annotationName, s -> new HashSet<>())
                        .add(typeName);
            }
        }
        for (Class<?> supertype : ReflectionUtils.getSuperTypes(type)) {
            String supertypeName = supertype.getName();
            if (subTypesStore.containsKey(supertypeName)) {
                subTypesStore.get(supertypeName).add(key);
            } else {
                subTypesStore
                        .computeIfAbsent(supertypeName, s -> new HashSet<>())
                        .add(key);
                expandSupertypes(subTypesStore, typesAnnotatedStore, supertypeName, supertype);
            }
        }
    }

    /**
     * apply {@link QueryFunction} on {@link Store}
     * <pre>{@code Set<T> ts = get(query)}</pre>
     * <p>use {@link Scanners} and {@link ReflectionUtils} query functions, such as:
     * <pre>{@code
     * Set<String> annotated = get(Scanners.TypesAnnotated.with(A.class))
     * Set<Class<?>> subtypes = get(Scanners.SubTypes.of(B.class).asClass())
     * Set<Method> methods = get(ReflectionUtils.Methods.of(B.class))
     * }</pre>
     */
    public <T> Set<T> get(QueryFunction<Store, T> query) {
        return query.apply(store);
    }

    ClassLoader[] loaders() {
        return configuration.getClassLoaders();
    }
}

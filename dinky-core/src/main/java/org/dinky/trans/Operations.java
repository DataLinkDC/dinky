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

package org.dinky.trans;

import org.dinky.parser.SqlType;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import lombok.extern.slf4j.Slf4j;

/**
 * Operations
 *
 * @since 2021/5/25 15:50
 */
@Slf4j
public class Operations {

    public static final String SQL_EMPTY_STR = "[\\s\\t\\n\\r]";

    private Operations() {}

    private static final Operation[] ALL_OPERATIONS = getAllOperations();

    /**
     * get all {@link Operation} children ordinary class,
     *
     * @return all operation class define in project.
     */
    private static Operation[] getAllOperations() {
        Reflections reflections = new Reflections(Operation.class.getPackage().getName());
        Set<Class<?>> operations =
                reflections.get(Scanners.SubTypes.of(Operation.class).asClass());

        return operations.stream()
                .filter(t -> !t.isInterface())
                .map(t -> {
                    try {
                        return (Operation) t.getConstructor().newInstance();
                    } catch (InstantiationException
                            | IllegalAccessException
                            | InvocationTargetException
                            | NoSuchMethodException e) {
                        log.error(String.format("getAllOperations error, class %s, err: %s", t, e));
                        throw new RuntimeException(e);
                    } catch (NoClassDefFoundError e) {
                        log.warn(
                                "getAllOperations error,  If you do not have this class, please add the corresponding dependency. Operation: {}.{}",
                                t,
                                e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(Operation[]::new);
    }

    public static SqlType getOperationType(String sql) {
        String sqlTrim = sql.replaceAll(SQL_EMPTY_STR, " ").trim().toUpperCase();
        return Arrays.stream(SqlType.values())
                .filter(sqlType -> sqlType.match(sqlTrim))
                .findFirst()
                .orElse(SqlType.UNKNOWN);
    }

    public static Operation buildOperation(String statement) {
        String sql = statement.replace("\n", " ").replaceAll("\\s+", " ").trim().toUpperCase();

        return Arrays.stream(ALL_OPERATIONS)
                .filter(p -> p.getHandle() != null && sql.startsWith(p.getHandle()))
                .findFirst()
                .map(p -> p.create(statement))
                .orElse(null);
    }
}

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

package org.dinky.sandbox.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Object stream that only reconstructs well-defined Sandbox Socket payload types.
 */
public class SandboxPayloadInputStream extends ObjectInputStream {

    private static final Set<String> ALLOWED_EXACT;
    private static final String[] ALLOWED_PREFIXES = {
        "java.lang.",
        "java.math.",
        "java.time.",
        "java.sql.",
        "org.apache.flink.types.",
        "org.dinky.data.socket.",
        "org.dinky.sandbox.metadata.",
        "org.dinky.sandbox.socket."
    };

    static {
        Set<String> exact = new HashSet<>();
        Collections.addAll(
                exact,
                "boolean",
                "byte",
                "char",
                "double",
                "float",
                "int",
                "long",
                "short",
                "void",
                "java.util.ArrayList",
                "java.util.Arrays$ArrayList",
                "java.util.Collections$EmptyList",
                "java.util.Collections$EmptyMap",
                "java.util.Collections$EmptySet",
                "java.util.Collections$SingletonList",
                "java.util.Collections$SingletonMap",
                "java.util.Collections$UnmodifiableCollection",
                "java.util.Collections$UnmodifiableList",
                "java.util.Collections$UnmodifiableMap",
                "java.util.Collections$UnmodifiableRandomAccessList",
                "java.util.Collections$UnmodifiableSet",
                "java.util.Date",
                "java.util.HashMap",
                "java.util.LinkedHashMap",
                "java.util.LinkedList",
                "java.util.Optional",
                "java.util.UUID");
        ALLOWED_EXACT = Collections.unmodifiableSet(exact);
    }

    public SandboxPayloadInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String className = desc.getName();
        if (!isAllowed(className)) {
            throw new InvalidClassException("Unsupported sandbox payload type: " + className);
        }
        return super.resolveClass(desc);
    }

    static boolean isAllowed(String className) {
        if (className == null || className.isEmpty()) {
            return false;
        }
        String name = className;
        while (name.startsWith("[")) {
            if (name.length() == 2 && "ZBCDFIJS".indexOf(name.charAt(1)) >= 0) {
                return true;
            }
            if (name.startsWith("[L") && name.endsWith(";")) {
                name = name.substring(2, name.length() - 1);
                continue;
            }
            name = name.substring(1);
        }
        if (ALLOWED_EXACT.contains(name)) {
            return true;
        }
        return Arrays.stream(ALLOWED_PREFIXES).anyMatch(name::startsWith);
    }
}

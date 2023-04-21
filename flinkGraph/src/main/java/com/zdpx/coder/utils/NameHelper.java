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

package com.zdpx.coder.utils;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.CaseFormat;

/** */
public final class NameHelper {
    private NameHelper() {}

    private static final AtomicInteger counter = new AtomicInteger();

    public static String generateVariableName(String name) {
        String gn = generateVariableName(name, "");
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, gn);
    }

    public static String generateVariableName(String name, String suffix) {
        return generateVariableName("_", name, suffix);
    }

    public static String generateVariableName(String prefix, String name, String suffix) {
        return prefix + name + counter.incrementAndGet() + suffix;
    }

    public static AtomicInteger getCounter() {
        return counter;
    }
}

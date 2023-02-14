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

package com.dlink.context;

import com.dlink.executor.CustomTableEnvironment;

/**
 * CustomTableEnvironmentContext
 *
 * @author wenmo
 * @since 2023/2/9
 */
public class CustomTableEnvironmentContext {

    private static final ThreadLocal<CustomTableEnvironment> CUSTOM_TABLE_ENVIRONMENT_CONTEXT = new ThreadLocal<>();

    public static void set(CustomTableEnvironment value) {
        CUSTOM_TABLE_ENVIRONMENT_CONTEXT.set(value);
    }

    public static CustomTableEnvironment get() {
        return CUSTOM_TABLE_ENVIRONMENT_CONTEXT.get();
    }

    public static void clear() {
        CUSTOM_TABLE_ENVIRONMENT_CONTEXT.remove();
    }
}

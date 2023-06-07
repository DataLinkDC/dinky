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

package org.dinky.context;

import java.io.File;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/** @since 0.7.0 */
public class FlinkUdfPathContextHolder {

    private static final ThreadLocal<Set<File>> UDF_PATH_CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Set<File>> OTHER_PLUGINS_PATH_CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Set<File>> PYTHON_UDF_FILE = new ThreadLocal<>();

    public static void addUdfPath(File file) {
        getUdfFile().add(file);
    }

    public static void addPyUdfPath(File file) {
        getPyUdfFile().add(file);
    }

    public static void addOtherPlugins(File file) {
        getOtherPluginsFiles().add(file);
    }

    public static Set<File> getUdfFile() {
        if (UDF_PATH_CONTEXT.get() == null) {
            UDF_PATH_CONTEXT.set(new CopyOnWriteArraySet<>());
        }
        return UDF_PATH_CONTEXT.get();
    }

    public static Set<File> getPyUdfFile() {
        if (PYTHON_UDF_FILE.get() == null) {
            PYTHON_UDF_FILE.set(new CopyOnWriteArraySet<>());
        }
        return PYTHON_UDF_FILE.get();
    }

    public static Set<File> getOtherPluginsFiles() {
        if (OTHER_PLUGINS_PATH_CONTEXT.get() == null) {
            OTHER_PLUGINS_PATH_CONTEXT.set(new CopyOnWriteArraySet<>());
        }
        return OTHER_PLUGINS_PATH_CONTEXT.get();
    }

    public static void clear() {
        UDF_PATH_CONTEXT.remove();
        OTHER_PLUGINS_PATH_CONTEXT.remove();
    }
}

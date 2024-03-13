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
import java.util.HashSet;
import java.util.Set;

/**
 * @since 0.7.0
 */
public class FlinkUdfPathContextHolder {

    private final Set<File> UDF_PATH_CONTEXT = new HashSet<>();
    private final Set<File> OTHER_PLUGINS_PATH_CONTEXT = new HashSet<>();
    private final Set<File> PYTHON_UDF_FILE = new HashSet<>();
    private final Set<File> FILES = new HashSet<>();

    public void addUdfPath(File file) {
        getUdfFile().add(file);
    }

    public void addFile(File file) {
        getFiles().add(file);
    }

    public void addPyUdfPath(File file) {
        getPyUdfFile().add(file);
    }

    public void addOtherPlugins(File file) {
        getOtherPluginsFiles().add(file);
    }

    public Set<File> getUdfFile() {
        return UDF_PATH_CONTEXT;
    }

    public Set<File> getPyUdfFile() {
        return PYTHON_UDF_FILE;
    }

    public Set<File> getOtherPluginsFiles() {
        return OTHER_PLUGINS_PATH_CONTEXT;
    }

    public Set<File> getAllFileSet() {
        Set<File> allFileSet = new HashSet<>();
        allFileSet.addAll(getUdfFile());
        allFileSet.addAll(getOtherPluginsFiles());
        allFileSet.addAll(getFiles());
        return allFileSet;
    }

    public Set<File> getFiles() {
        return FILES;
    }
}

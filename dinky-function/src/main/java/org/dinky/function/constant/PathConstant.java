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

package org.dinky.function.constant;

import org.dinky.data.constant.DirConstant;

import org.apache.flink.table.catalog.FunctionLanguage;

import java.io.File;

import cn.hutool.core.util.StrUtil;

public class PathConstant {

    /** Tmp path */
    public static final String TMP_PATH = DirConstant.getTempRootDir() + File.separator;

    /** UDF path */
    public static final String UDF_PATH = TMP_PATH + "udf" + File.separator;

    public static final String TASK_PATH = TMP_PATH + "task";

    public static final String COMPILER = "compiler";
    public static final String PACKAGE = "package";
    /** UDF jar rules */
    public static final String UDF_JAR_RULE = "udf-\\d+.jar";
    /** UDF version rules */
    public static final String UDF_VERSION_RULE = "\\d+";
    /**Udf jar tmp name */
    public static final String UDF_JAR_TMP_NAME = "udf-tmp.jar";

    public static final String UDF_JAR_NAME = "udf.jar";
    public static final String DEP_MANIFEST = "dep_manifest.json";
    public static final String DEP_ZIP = "dep.zip";
    public static final String UDF_PYTHON_NAME = "python_udf.zip";
    /** udf jar tmp路径 */
    public static final String UDF_JAR_TMP_PATH = UDF_PATH + UDF_JAR_TMP_NAME;

    public static String getPath(Object... path) {
        return StrUtil.join(File.separator, path) + File.separator;
    }

    public static String getUdfCompilerPath(FunctionLanguage language, String fileName) {
        return getPath(UDF_PATH, COMPILER, language.name(), fileName);
    }

    public static String getUdfCompilerPath(FunctionLanguage language) {
        return getPath(UDF_PATH, COMPILER, language.name());
    }

    public static String getUdfPackagePath(Integer taskId, Object... path) {
        return getPath(UDF_PATH, taskId, PACKAGE, path);
    }

    public static String getTaskUdfPath(Integer taskId) {
        return getPath(TASK_PATH, taskId, "udf");
    }
}

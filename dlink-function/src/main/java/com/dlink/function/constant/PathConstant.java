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

package com.dlink.function.constant;

import java.io.File;

import cn.hutool.core.util.StrUtil;

/**
 * 文件路径常量
 *
 * @author ZackYoung
 * @since 0.6.8
 */
public class PathConstant {

    /**
     * 基本路径，dinky 部署的路径
     */
    public static final String WORK_DIR = System.getProperty("user.dir");
    /**
     * tmp路径
     */
    public static final String TMP_PATH = WORK_DIR + File.separator + "tmp" + File.separator;

    /**
     * udf路径
     */
    public static final String UDF_PATH = TMP_PATH + "udf" + File.separator;
    public static final String COMPILER = "compiler";
    public static final String PACKAGE = "package";
    /**
     * udf jar规则
     */
    public static final String UDF_JAR_RULE = "udf-\\d+.jar";
    /**
     * udf版本规则
     */
    public static final String UDF_VERSION_RULE = "\\d+";
    /**
     * udf jar tmp名字
     */
    public static final String UDF_JAR_TMP_NAME = "udf-tmp.jar";
    public static final String UDF_JAR_NAME = "udf.jar";
    public static final String UDF_MANIFEST = "udf_manifest.json";
    public static final String UDF_PYTHON_NAME = "python_udf.zip";
    /**
     * udf jar tmp路径
     */
    public static final String UDF_JAR_TMP_PATH = UDF_PATH + UDF_JAR_TMP_NAME;

    public static String getPath(Object... path) {
        return StrUtil.join(File.separator, path) + File.separator;
    }

    public static String getUdfCompilerJavaPath(Integer missionId, Object... path) {
        return getPath(UDF_PATH, missionId, COMPILER, "java", path);
    }

    public static String getUdfCompilerPythonPath(Integer missionId, Object... path) {
        return getPath(UDF_PATH, missionId, COMPILER, "python", path);
    }

    public static String getUdfPackagePath(Integer missionId, Object... path) {
        return getPath(UDF_PATH, missionId, PACKAGE, path);
    }

}

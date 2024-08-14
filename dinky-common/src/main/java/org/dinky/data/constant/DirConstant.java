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

package org.dinky.data.constant;

import java.io.File;

import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;

/**
 * DirConstant
 *
 * @since 2022/10/15 18:37
 */
public class DirConstant {
    public static final String DINKY_ENV_OR_HOME_ROOT_DIR_NAME = "dinky.root.path";
    public static final String FILE_SEPARATOR = File.separator;

    // user dir path, default is project root path
    public static final String USER_DIR_ENV_ROOT_PATH = System.getProperty(SystemUtil.USER_DIR);

    // dinky env or home root path
    public static final String DINKY_ENV_OR_HOME_ROOT_DIR = System.getProperty(DINKY_ENV_OR_HOME_ROOT_DIR_NAME);

    /**
     * get a root path of dinky
     * @return root path of dinky
     */
    public static String getRootPath() {
        return StrUtil.isEmpty(DINKY_ENV_OR_HOME_ROOT_DIR) ? USER_DIR_ENV_ROOT_PATH : DINKY_ENV_OR_HOME_ROOT_DIR;
    }

    /**
     * get a root logs path of dinky
     * @return root logs path of dinky
     */
    public static String getRootLogsPath() {
        return getRootPath() + FILE_SEPARATOR + "logs";
    }

    /**
     * get root log  of dinky
     * @return root log of dinky
     */
    public static String getRootLog() {
        return getRootLogsPath() + FILE_SEPARATOR + "dinky.log";
    }

    /**
     * get tmp dir root of dinky
     * @return tmp dir root of dinky
     */
    public static String getTempRootDir() {
        return getRootPath() + FILE_SEPARATOR + "tmp";
    }
}

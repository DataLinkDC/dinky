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

import cn.hutool.system.SystemUtil;

/**
 * DirConstant
 *
 * @since 2022/10/15 18:37
 */
public class DirConstant {

    public static final String FILE_SEPARATOR = File.separator;
    public static final String ROOT_PATH = System.getProperty(SystemUtil.USER_DIR);
    public static final String LOG_DIR_PATH = ROOT_PATH + FILE_SEPARATOR + "logs";
    public static final String ROOT_LOG_PATH = LOG_DIR_PATH + FILE_SEPARATOR + "dinky.log";
}

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

package com.dlink.constant;

import org.springframework.boot.system.ApplicationHome;

/**
 * DirConstant
 *
 * @author wenmo
 * @since 2022/10/15 18:37
 */
public class DirConstant {

    public static final String FILE_SEPARATOR = "file.separator";
    public static final String LOG_DIR_PATH;
    public static final String ROOT_LOG_PATH;

    static {
        String separator = System.getProperty(FILE_SEPARATOR);
        // String rootPath = new ApplicationHome(Dlink.class).getSource().getParent();
        String rootPath = new ApplicationHome().getDir().getPath();
        LOG_DIR_PATH = rootPath + separator + "logs";
        ROOT_LOG_PATH = LOG_DIR_PATH + separator + "dlink.log";
    }
}

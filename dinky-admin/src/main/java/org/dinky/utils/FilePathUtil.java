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

package org.dinky.utils;

import org.dinky.data.constant.UploadFileConstant;

import org.apache.commons.lang3.StringUtils;

/** File path handle */
public class FilePathUtil {

    /**
     * Add a file separator '/' at the end of the file path
     *
     * @param filePath File path
     */
    public static String addFileSeparator(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        } else {
            if (filePath.endsWith("/")) {
                return filePath;
            } else {
                return filePath + "/";
            }
        }
    }

    /**
     * Remove a file separator '/' at the end of the file path
     *
     * @param filePath File path
     */
    public static String removeFileSeparator(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        } else {
            if (filePath.endsWith("/")) {
                return filePath.substring(0, filePath.length() - 1);
            } else {
                return filePath;
            }
        }
    }

    /**
     * Get dir type, refer {@link UploadFileConstant}
     *
     * @param dir Directory
     * @return Refer {@link UploadFileConstant}
     */
    public static byte getDirTarget(String dir) {
        if (StringUtils.isEmpty(dir)) {
            return UploadFileConstant.TARGET_OTHER;
        } else if (dir.contains("hdfs")) {
            return UploadFileConstant.TARGET_HDFS;
        } else {
            return UploadFileConstant.TARGET_LOCAL;
        }
    }
}

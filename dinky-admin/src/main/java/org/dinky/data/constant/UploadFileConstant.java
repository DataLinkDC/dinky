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

import org.springframework.boot.system.ApplicationHome;

/** Upload file's some constant. */
public class UploadFileConstant {

    // Upload file's type
    // constant----------------------------------------------------------------------------------------
    /** Not internal upload file type, this value represent upload the file to the specific dir. */
    public static final byte TYPE_OTHER = -1;

    public static final byte HADOOP_CONF_ID = 1;
    public static final String HADOOP_CONF_NAME = "hadoop-conf";
    public static final byte FLINK_CONF_ID = 2;
    public static final String FLINK_CONF_NAME = "flink-conf";
    public static final byte FLINK_LIB_ID = 3;
    public static final String FLINK_LIB_NAME = "flink-lib";
    public static final byte USER_JAR_ID = 4;
    public static final String USER_JAR_NAME = "user-jar";
    public static final byte DINKY_JAR_ID = 5;
    public static final String DINKY_JAR_NAME = "dinky-jar";

    // Upload file's dir
    // constant----------------------------------------------------------------------------------------
    static {
        // Get admin jar's parent absolute path
        DINKY_HOME_DIR =
                new ApplicationHome(UploadFileConstant.class).getSource().getParent() + "/../";
    }

    public static final String DINKY_HOME_DIR;
    public static final String HDFS_HOME_DIR = "hdfs:///";
    public static final String HADOOP_CONF_DIR = DINKY_HOME_DIR + "/config/hadoop-conf";
    public static final String FLINK_CONF_DIR = DINKY_HOME_DIR + "/config/flink-conf";
    public static final String FLINK_LIB_DIR = HDFS_HOME_DIR + "dinky/jar/flink/lib";
    public static final String DINKY_JAR_DIR = HDFS_HOME_DIR + "dinky/jar/dinky";
    public static final String USER_JAR_DIR = HDFS_HOME_DIR + "dinky/jar/user";

    // Upload file's target
    // constant----------------------------------------------------------------------------------------
    /** An unidentified upload file type */
    public static final byte TARGET_OTHER = -1;

    public static final byte TARGET_LOCAL = 1;
    public static final byte TARGET_HDFS = 2;

    /**
     * Get internal upload file type's dir name
     *
     * @param fileType Upload file type id.
     * @return Internal upload file dir name
     */
    public static String getDirName(byte fileType) {
        switch (fileType) {
            case HADOOP_CONF_ID:
                return HADOOP_CONF_NAME;
            case FLINK_CONF_ID:
                return FLINK_CONF_NAME;
            case FLINK_LIB_ID:
                return FLINK_LIB_NAME;
            case USER_JAR_ID:
                return USER_JAR_NAME;
            case DINKY_JAR_ID:
                return DINKY_JAR_NAME;
            default:
                return null;
        }
    }

    /**
     * Get internal upload file type's dir path
     *
     * @param fileType Upload file type id.
     * @return Internal upload file dir path
     */
    public static String getDirPath(byte fileType) {
        switch (fileType) {
            case HADOOP_CONF_ID:
                return HADOOP_CONF_DIR;
            case FLINK_CONF_ID:
                return FLINK_CONF_DIR;
            case FLINK_LIB_ID:
                return FLINK_LIB_DIR;
            case USER_JAR_ID:
                return USER_JAR_DIR;
            case DINKY_JAR_ID:
                return DINKY_JAR_DIR;
            default:
                return "";
        }
    }

    /**
     * Get internal upload file type's target
     *
     * @param fileType Upload file type id.
     * @return Upload file target
     */
    public static byte getTarget(byte fileType) {
        switch (fileType) {
            case HADOOP_CONF_ID:
            case FLINK_CONF_ID:
                return TARGET_LOCAL;
            case FLINK_LIB_ID:
            case USER_JAR_ID:
            case DINKY_JAR_ID:
                return TARGET_HDFS;
            default:
                return TARGET_OTHER;
        }
    }
}

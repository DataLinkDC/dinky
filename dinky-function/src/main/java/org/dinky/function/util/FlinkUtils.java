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

package org.dinky.function.util;

import org.apache.flink.runtime.util.EnvironmentInformation;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;

/**
 * @since 0.6.8
 */
public class FlinkUtils {

    public static String getFlinkVersion() {
        return EnvironmentInformation.getVersion();
    }

    /**
     * @param version flink version。如：1.14.6
     * @return flink 大版本，如 14
     */
    public static String getFlinkBigVersion(String version) {
        return StrUtil.split(version, ".").get(1);
    }

    /** @return 获取当前 flink 大版本 */
    public static Integer getCurFlinkBigVersion() {
        return Convert.toInt(getFlinkBigVersion(getFlinkVersion()));
    }
}

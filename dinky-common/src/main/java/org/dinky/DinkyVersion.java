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

package org.dinky;

import cn.hutool.setting.dialect.Props;

public final class DinkyVersion {
    private DinkyVersion() {}

    /**
     * Return the full version string of the present Dinky codebase.
     *
     * @return the version of Dinky
     */
    public static String getVersion() {
        try {
            return Props.getProp("common.properties").getProperty("version");
        } catch (Exception e) {
            return "${revision}";
        }
    }

    /**
     * Return the short version string of the present Dinky codebase.
     * @return the short version of Dinky
     */
    public static String getShortVersion() {
        try {
            String version = getVersion();
            int idx = version.indexOf('-');
            if (idx > 0) {
                return version.substring(0, idx);
            }
            return version;
        } catch (Exception e) {
            return "${revision}";
        }
    }
}

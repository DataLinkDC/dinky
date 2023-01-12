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

package org.dinky.cluster;

import lombok.Getter;
import lombok.Setter;

/**
 * FlinkClusterInfo
 *
 * @author wenmo
 * @since 2021/10/20 9:10
 */
@Getter
@Setter
public class FlinkClusterInfo {

    private boolean isEffective;
    private String jobManagerAddress;
    private String version;

    public static final FlinkClusterInfo INEFFECTIVE = new FlinkClusterInfo(false);

    public FlinkClusterInfo(boolean isEffective) {
        this.isEffective = isEffective;
    }

    public FlinkClusterInfo(boolean isEffective, String jobManagerAddress, String version) {
        this.isEffective = isEffective;
        this.jobManagerAddress = jobManagerAddress;
        this.version = version;
    }

    public static FlinkClusterInfo build(String jobManagerAddress, String version) {
        return new FlinkClusterInfo(true, jobManagerAddress, version);
    }
}

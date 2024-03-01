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

package org.dinky.data.enums;

import org.dinky.assertion.Asserts;

/**
 * SubmitType
 *
 * @since 2021/10/29
 */
public enum GatewayType {
    LOCAL("l", "local"),
    STANDALONE("s", "standalone"),
    YARN_SESSION("ys", "yarn-session"),
    YARN_APPLICATION("ya", "yarn-application"),
    YARN_PER_JOB("ypj", "yarn-per-job"),
    KUBERNETES_SESSION("ks", "kubernetes-session"),
    KUBERNETES_APPLICATION("ka", "kubernetes-application"),
    KUBERNETES_APPLICATION_OPERATOR("kao", "kubernetes-application-operator");

    private final String value;
    private final String longValue;

    GatewayType(String value, String longValue) {
        this.value = value;
        this.longValue = longValue;
    }

    public String getValue() {
        return value;
    }

    public String getLongValue() {
        return longValue;
    }

    public static GatewayType get(String value) {
        for (GatewayType type : GatewayType.values()) {
            if (Asserts.isEquals(type.getValue(), value) || Asserts.isEquals(type.getLongValue(), value)) {
                return type;
            }
        }
        return GatewayType.LOCAL;
    }

    public static GatewayType getSessionType(String value) {
        if (value.contains("kubernetes")) {
            return GatewayType.KUBERNETES_SESSION;
        }
        return GatewayType.YARN_SESSION;
    }

    public GatewayType getSessionType() {
        if (longValue.contains("kubernetes")) {
            return GatewayType.KUBERNETES_SESSION;
        }
        return GatewayType.YARN_SESSION;
    }

    public boolean equalsValue(String type) {
        return Asserts.isEquals(value, type) || Asserts.isEquals(longValue, type);
    }

    public static boolean isDeployCluster(String type) {
        return get(type).isDeployCluster();
    }

    /**
     * 是否在本地构建 job graph , 用于校验是否提交到集群
     * @return true: 本地构建 jobgraph
     */
    public boolean isLocalExecute() {
        switch (value) {
            case "l":
            case "ya":
            case "ypj":
            case "ka":
            case "kao":
                return true;
            default:
                return false;
        }
    }

    public boolean isDeployCluster() {
        switch (this) {
            case YARN_APPLICATION:
            case YARN_PER_JOB:
            case KUBERNETES_APPLICATION:
            case KUBERNETES_APPLICATION_OPERATOR:
                return true;
            default:
                return false;
        }
    }

    public boolean isApplicationMode() {
        switch (this) {
            case YARN_APPLICATION:
            case KUBERNETES_APPLICATION:
            case KUBERNETES_APPLICATION_OPERATOR:
                return true;
            default:
                return false;
        }
    }

    public boolean isKubernetesApplicationMode() {
        switch (this) {
            case KUBERNETES_APPLICATION:
            case KUBERNETES_APPLICATION_OPERATOR:
                return true;
            default:
                return false;
        }
    }
}

package com.dlink.config;

import com.dlink.assertion.Asserts;

public enum ClusterType {

    LOCAL("local"),STANDALONE("standalone"),YARN_SESSION("yarn-session"),
    YARN_PER_JOB("yarn-per-job"),YARN_APPLICATION("yarn-application"),
    KUBERNETES_SESSION("kubernetes-session"), KubernetesApplaction("kubernetes-application");

    private String value;

    ClusterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean equalsVal(String valueText) {
        return Asserts.isEqualsIgnoreCase(value, valueText);
    }

}

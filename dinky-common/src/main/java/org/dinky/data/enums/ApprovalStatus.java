package org.dinky.data.enums;

import org.dinky.assertion.Asserts;

import java.util.Arrays;

public enum ApprovalStatus {
    UNKNOWN("UNKNOWN"),
    CREATED("CREATED"),
    SUBMITTED("SUBMITTED"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CANCELED("CANCELED")
    ;
    private final String value;

    ApprovalStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ApprovalStatus fromValue(String value) {
        return Arrays.stream(ApprovalStatus.values())
                .filter(type -> Asserts.isEqualsIgnoreCase(type.getValue(), value))
                .findFirst()
                .orElse(ApprovalStatus.UNKNOWN);
    }

    public static boolean isInProcess(ApprovalStatus status) {
        return status.equals(ApprovalStatus.SUBMITTED);
    }

    public boolean equalVal(String value) {
        return this.value.equals(value);
    }
}

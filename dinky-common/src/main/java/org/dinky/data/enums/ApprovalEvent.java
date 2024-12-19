package org.dinky.data.enums;

public enum ApprovalEvent {
    UNKNOWN("UNKNOWN"),
    SUBMIT("SUBMIT"),
    APPROVE("APPROVE"),
    REJECT("REJECT"),
    WITHDRAW("WITHDRAW"),
    CANCEL("CANCEL");
    private final String value;
    ApprovalEvent(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}

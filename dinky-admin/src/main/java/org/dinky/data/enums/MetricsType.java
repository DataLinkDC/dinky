package org.dinky.data.enums;

public enum MetricsType {
    LOCAL( "local"),
    FLINK( "flink");

    private final String type;

    public String getType() {
        return this.type;
    }

    MetricsType(String type) {
        this.type = type;
    }


}

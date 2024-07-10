package org.dinky.metadata.config;

public enum FileSystemType {
    LOCAL("local"),
    HDFS("hdfs"),
    S3("s3"),
    OSS("oss"),
    ;

    private final String type;

    FileSystemType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static FileSystemType fromType(String type) {
        for (FileSystemType value : FileSystemType.values()) {
            if (value.getType().equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }
}

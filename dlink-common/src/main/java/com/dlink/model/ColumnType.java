package com.dlink.model;

/**
 * ColumnType
 *
 * @author wenmo
 * @since 2022/2/17 10:59
 **/
public enum ColumnType {

    STRING("java.lang.String", "STRING"),
    JAVA_LANG_BOOLEAN("java.lang.Boolean", "BOOLEAN"),
    BOOLEAN("Boolean", "BOOLEAN NOT NULL"),
    JAVA_LANG_BYTE("java.lang.Byte", "TINYINT"),
    BYTE("byte", "TINYINT NOT NULL"),
    JAVA_LANG_SHORT("java.lang.Short", "SMALLINT"),
    SHORT("short", "SMALLINT NOT NULL"),
    INTEGER("java.lang.Integer", "INT"),
    INT("int", "INT NOT NULL"),
    JAVA_LANG_LONG("java.lang.Long", "BIGINT"),
    LONG("long", "BIGINT NOT NULL"),
    JAVA_LANG_FLOAT("java.lang.Float", "FLOAT"),
    FLOAT("float", "FLOAT NOT NULL"),
    JAVA_LANG_DOUBLE("java.lang.Double", "DOUBLE"),
    DOUBLE("double", "DOUBLE NOT NULL"),
    DATE("java.sql.Date", "DATE"),
    LOCALDATE("java.time.LocalDate", "DATE"),
    TIME("java.sql.Time", "TIME(0)"),
    LOCALTIME("java.time.LocalTime", "TIME(9)"),
    TIMESTAMP("java.sql.Timestamp", "TIMESTAMP(9)"),
    LOCALDATETIME("java.time.LocalDateTime", "TIMESTAMP(9)"),
    OFFSETDATETIME("java.time.OffsetDateTime", "TIMESTAMP(9) WITH TIME ZONE"),
    INSTANT("java.time.Instant", "TIMESTAMP_LTZ(9)"),
    DURATION("java.time.Duration", "INVERVAL SECOND(9)"),
    PERIOD("java.time.Period", "INTERVAL YEAR(4) TO MONTH"),
    DECIMAL("java.math.BigDecimal", "DECIMAL"),
    BYTES("byte[]", "BYTES"),
    T("T[]", "ARRAY<T>"),
    MAP("java.util.Map<K, V>", "MAP<K, V>");

    private String javaType;
    private String flinkType;
    private Integer precision;
    private Integer scale;

    ColumnType(String javaType, String flinkType) {
        this.javaType = javaType;
        this.flinkType = flinkType;
    }

    public ColumnType setPrecisionAndScale(Integer precision, Integer scale) {
        this.precision = precision;
        this.scale = scale;
        return this;
    }

    public String getJavaType() {
        return javaType;
    }

    public String getFlinkType() {
        return flinkType;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScala(Integer scale) {
        this.scale = scale;
    }
}

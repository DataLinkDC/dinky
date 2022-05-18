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
    TIME("java.sql.Time", "TIME"),
    LOCALTIME("java.time.LocalTime", "TIME"),
    TIMESTAMP("java.sql.Timestamp", "TIMESTAMP"),
    LOCALDATETIME("java.time.LocalDateTime", "TIMESTAMP"),
    OFFSETDATETIME("java.time.OffsetDateTime", "TIMESTAMP WITH TIME ZONE"),
    INSTANT("java.time.Instant", "TIMESTAMP_LTZ"),
    DURATION("java.time.Duration", "INVERVAL SECOND"),
    PERIOD("java.time.Period", "INTERVAL YEAR TO MONTH"),
    DECIMAL("java.math.BigDecimal", "DECIMAL"),
    BYTES("byte[]", "BYTES"),
    T("T[]", "ARRAY"),
    MAP("java.util.Map<K, V>", "MAP");

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
        if (flinkType.equals("DECIMAL")) {
            if (precision == null || precision == 0) {
                return flinkType + "(" + 38 + "," + scale + ")";
            } else {
                return flinkType + "(" + precision + "," + scale + ")";
            }
        } else {
            return flinkType;
        }
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

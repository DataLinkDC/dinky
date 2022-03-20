package com.dlink.config;

import com.dlink.assertion.Asserts;

/**
 * Dialect
 *
 * @author wenmo
 * @since 2021/12/13
 **/
public enum Dialect {

    FLINKSQL("FlinkSql"), FLINKJAR("FlinkJar"), FLINKSQLENV("FlinkSqlEnv"), SQL("Sql"), JAVA("Java"),
    MYSQL("Mysql"), ORACLE("Oracle"), SQLSERVER("SqlServer"), POSTGRESQL("PostGreSql"), CLICKHOUSE("ClickHouse"),
    DORIS("Doris"), PHOENIX("Phoenix"), HIVE("Hive");

    private String value;

    public static final Dialect DEFAULT = Dialect.FLINKSQL;

    Dialect(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean equalsVal(String valueText) {
        return Asserts.isEqualsIgnoreCase(value, valueText);
    }

    public static Dialect get(String value) {
        for (Dialect type : Dialect.values()) {
            if (Asserts.isEqualsIgnoreCase(type.getValue(), value)) {
                return type;
            }
        }
        return Dialect.FLINKSQL;
    }

    public static boolean isSql(String value) {
        Dialect dialect = Dialect.get(value);
        switch (dialect) {
            case SQL:
            case MYSQL:
            case ORACLE:
            case SQLSERVER:
            case POSTGRESQL:
            case CLICKHOUSE:
            case DORIS:
            case PHOENIX:
            case HIVE:
                return true;
            default:
                return false;
        }
    }
}


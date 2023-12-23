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

package org.dinky.config;

import org.dinky.assertion.Asserts;

/**
 * Dialect
 *
 * @since 2021/12/13
 */
public enum Dialect {
    FLINK_SQL("FlinkSql"),
    FLINK_JAR("FlinkJar"),
    FLINK_SQL_ENV("FlinkSqlEnv"),
    SQL("Sql"),
    JAVA("Java"),
    PYTHON("Python"),
    SCALA("Scala"),
    MYSQL("Mysql"),
    ORACLE("Oracle"),
    SQLSERVER("SqlServer"),
    POSTGRESQL("PostgreSql"),
    CLICKHOUSE("ClickHouse"),
    DORIS("Doris"),
    PHOENIX("Phoenix"),
    HIVE("Hive"),
    STAR_ROCKS("StarRocks"),
    PRESTO("Presto"),
    KUBERNETES_APPLICATION("KubernetesApplication");

    private String value;

    public static final Dialect DEFAULT = Dialect.FLINK_SQL;

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
        return Dialect.FLINK_SQL;
    }

    /**
     * Judge sql dialect.
     *
     * @param value {@link Dialect}
     * @return If is flink sql, return false, otherwise return true.
     */
    public static boolean isCommonSql(String value) {
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
            case STAR_ROCKS:
            case PRESTO:
                return true;
            default:
                return false;
        }
    }

    public static boolean isUDF(String value) {
        Dialect dialect = Dialect.get(value);
        switch (dialect) {
            case JAVA:
            case SCALA:
            case PYTHON:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFlinkSql(String value, boolean includeFlinksqlEnv) {
        Dialect dialect = Dialect.get(value);
        switch (dialect) {
            case FLINK_SQL:
            case FLINK_JAR:
                return true;
            case FLINK_SQL_ENV:
                return includeFlinksqlEnv;
            default:
                return false;
        }
    }

    public static boolean isJarDialect(String value) {
        Dialect dialect = Dialect.get(value);
        switch (dialect) {
            case FLINK_JAR:
            case KUBERNETES_APPLICATION:
                return true;
            default:
                return false;
        }
    }
}

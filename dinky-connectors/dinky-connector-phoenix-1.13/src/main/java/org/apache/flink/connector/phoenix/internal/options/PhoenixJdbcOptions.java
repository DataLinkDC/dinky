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

package org.apache.flink.connector.phoenix.internal.options;

import org.apache.flink.connector.phoenix.JdbcConnectionOptions;
import org.apache.flink.connector.phoenix.dialect.JdbcDialect;
import org.apache.flink.connector.phoenix.dialect.JdbcDialects;
import org.apache.flink.util.Preconditions;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * PhoenixJdbcOptions
 *
 * @since 2022/3/17 9:57
 */
public class PhoenixJdbcOptions extends JdbcConnectionOptions {
    private static final long serialVersionUID = 1L;
    private String tableName;
    private JdbcDialect dialect;
    @Nullable private final Integer parallelism;
    // setting phoenix schema isEnabled
    private Boolean isNamespaceMappingEnabled;
    private Boolean mapSystemTablesToNamespace;

    private PhoenixJdbcOptions(
            String dbURL,
            String tableName,
            String driverName,
            String username,
            String password,
            JdbcDialect dialect,
            Integer parallelism,
            int connectionCheckTimeoutSeconds,
            boolean isNamespaceMappingEnabled,
            boolean mapSystemTablesToNamespace) {
        super(dbURL, driverName, username, password, connectionCheckTimeoutSeconds);
        this.tableName = tableName;
        this.dialect = dialect;
        this.parallelism = parallelism;
        this.isNamespaceMappingEnabled = isNamespaceMappingEnabled;
        this.mapSystemTablesToNamespace = mapSystemTablesToNamespace;
    }

    public String getTableName() {
        return this.tableName;
    }

    public JdbcDialect getDialect() {
        return this.dialect;
    }

    public Integer getParallelism() {
        return this.parallelism;
    }

    public Boolean getNamespaceMappingEnabled() {
        return isNamespaceMappingEnabled;
    }

    public Boolean getMapSystemTablesToNamespace() {
        return mapSystemTablesToNamespace;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean equals(Object o) {
        if (!(o instanceof PhoenixJdbcOptions)) {
            return false;
        } else {
            PhoenixJdbcOptions options = (PhoenixJdbcOptions) o;
            return Objects.equals(this.url, options.url)
                    && Objects.equals(this.tableName, options.tableName)
                    && Objects.equals(this.driverName, options.driverName)
                    && Objects.equals(this.username, options.username)
                    && Objects.equals(this.password, options.password)
                    && Objects.equals(
                            this.dialect.getClass().getName(), options.dialect.getClass().getName())
                    && Objects.equals(this.parallelism, options.parallelism)
                    && Objects.equals(
                            this.connectionCheckTimeoutSeconds,
                            options.connectionCheckTimeoutSeconds)
                    && Objects.equals(
                            this.isNamespaceMappingEnabled, options.isNamespaceMappingEnabled)
                    && Objects.equals(
                            this.mapSystemTablesToNamespace, options.mapSystemTablesToNamespace);
        }
    }

    public int hashCode() {
        return Objects.hash(
                new Object[] {
                    this.url,
                    this.tableName,
                    this.driverName,
                    this.username,
                    this.password,
                    this.dialect.getClass().getName(),
                    this.parallelism,
                    this.connectionCheckTimeoutSeconds,
                    this.isNamespaceMappingEnabled,
                    this.mapSystemTablesToNamespace
                });
    }

    public static class Builder {
        private String dbURL;
        private String tableName;
        private String driverName;
        private String username;
        private String password;
        private JdbcDialect dialect;
        private Integer parallelism;
        private int connectionCheckTimeoutSeconds = 60;
        private Boolean isNamespaceMappingEnabled;
        private Boolean mapSystemTablesToNamespace;

        public Builder() {}

        public Builder setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setConnectionCheckTimeoutSeconds(int connectionCheckTimeoutSeconds) {
            this.connectionCheckTimeoutSeconds = connectionCheckTimeoutSeconds;
            return this;
        }

        public Builder setDriverName(String driverName) {
            this.driverName = driverName;
            return this;
        }

        public Builder setDBUrl(String dbURL) {
            this.dbURL = dbURL;
            return this;
        }

        public Builder setDialect(JdbcDialect dialect) {
            this.dialect = dialect;
            return this;
        }

        public Builder setParallelism(Integer parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        public Builder setNamespaceMappingEnabled(Boolean namespaceMappingEnabled) {
            this.isNamespaceMappingEnabled = namespaceMappingEnabled;
            return this;
        }

        public Builder setMapSystemTablesToNamespace(Boolean mapSystemTablesToNamespace) {
            this.mapSystemTablesToNamespace = mapSystemTablesToNamespace;
            return this;
        }

        public PhoenixJdbcOptions build() {
            Preconditions.checkNotNull(this.dbURL, "No dbURL supplied.");
            Preconditions.checkNotNull(this.tableName, "No tableName supplied.");
            Optional optional;
            if (this.dialect == null) {
                optional = JdbcDialects.get(this.dbURL);
                this.dialect =
                        (JdbcDialect)
                                optional.orElseGet(
                                        () -> {
                                            throw new NullPointerException(
                                                    "Unknown dbURL,can not find proper dialect.");
                                        });
            }

            if (this.driverName == null) {
                optional = this.dialect.defaultDriverName();
                this.driverName =
                        (String)
                                optional.orElseGet(
                                        () -> {
                                            throw new NullPointerException(
                                                    "No driverName supplied.");
                                        });
            }

            return new PhoenixJdbcOptions(
                    this.dbURL,
                    this.tableName,
                    this.driverName,
                    this.username,
                    this.password,
                    this.dialect,
                    this.parallelism,
                    this.connectionCheckTimeoutSeconds,
                    this.isNamespaceMappingEnabled,
                    this.mapSystemTablesToNamespace);
        }
    }
}

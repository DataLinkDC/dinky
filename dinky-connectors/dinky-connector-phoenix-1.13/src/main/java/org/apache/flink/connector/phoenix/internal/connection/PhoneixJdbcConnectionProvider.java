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

package org.apache.flink.connector.phoenix.internal.connection;

import org.apache.flink.connector.phoenix.JdbcConnectionOptions;
import org.apache.flink.util.Preconditions;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PhoneixJdbcConnectionProvider
 *
 * @since 2022/3/17 9:04
 */
public class PhoneixJdbcConnectionProvider implements JdbcConnectionProvider, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(PhoneixJdbcConnectionProvider.class);
    private static final long serialVersionUID = 1L;
    private final JdbcConnectionOptions jdbcOptions;
    private transient Driver loadedDriver;
    private transient Connection connection;
    private Boolean namespaceMappingEnabled;
    private Boolean mapSystemTablesEnabled;

    public PhoneixJdbcConnectionProvider(JdbcConnectionOptions jdbcOptions) {
        this.jdbcOptions = jdbcOptions;
    }

    public PhoneixJdbcConnectionProvider(
            JdbcConnectionOptions jdbcOptions,
            boolean namespaceMappingEnabled,
            boolean mapSystemTablesEnabled) {
        this.jdbcOptions = jdbcOptions;
        this.namespaceMappingEnabled = namespaceMappingEnabled;
        this.mapSystemTablesEnabled = mapSystemTablesEnabled;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public boolean isConnectionValid() throws SQLException {
        return this.connection != null
                && this.connection.isValid(this.jdbcOptions.getConnectionCheckTimeoutSeconds());
    }

    private static Driver loadDriver(String driverName)
            throws SQLException, ClassNotFoundException {
        Preconditions.checkNotNull(driverName);
        Enumeration drivers = DriverManager.getDrivers();

        Driver driver;
        do {
            if (!drivers.hasMoreElements()) {
                Class clazz =
                        Class.forName(
                                driverName, true, Thread.currentThread().getContextClassLoader());

                try {
                    return (Driver) clazz.newInstance();
                } catch (Exception var4) {
                    throw new SQLException("Fail to create driver of class " + driverName, var4);
                }
            }

            driver = (Driver) drivers.nextElement();
        } while (!driver.getClass().getName().equals(driverName));

        return driver;
    }

    private Driver getLoadedDriver() throws SQLException, ClassNotFoundException {
        if (this.loadedDriver == null) {
            this.loadedDriver = loadDriver(this.jdbcOptions.getDriverName());
        }

        return this.loadedDriver;
    }

    public Connection getOrEstablishConnection() throws SQLException, ClassNotFoundException {
        if (this.connection != null) {
            return this.connection;
        } else {
            if (this.jdbcOptions.getDriverName() == null) {
                this.connection =
                        DriverManager.getConnection(
                                this.jdbcOptions.getDbURL(),
                                (String) this.jdbcOptions.getUsername().orElse((String) null),
                                (String) this.jdbcOptions.getPassword().orElse((String) null));
            } else {
                Driver driver = this.getLoadedDriver();
                Properties info = new Properties();
                this.jdbcOptions
                        .getUsername()
                        .ifPresent(
                                (user) -> {
                                    info.setProperty("user", user);
                                });
                this.jdbcOptions
                        .getPassword()
                        .ifPresent(
                                (password) -> {
                                    info.setProperty("password", password);
                                });

                if (this.namespaceMappingEnabled && this.mapSystemTablesEnabled) {
                    info.setProperty("phoenix.schema.isNamespaceMappingEnabled", "true");
                    info.setProperty("phoenix.schema.mapSystemTablesToNamespace", "true");
                }

                this.connection = driver.connect(this.jdbcOptions.getDbURL(), info);

                this.connection.setAutoCommit(false);
                if (this.connection == null) {
                    throw new SQLException(
                            "No suitable driver found for " + this.jdbcOptions.getDbURL(), "08001");
                }
            }

            return this.connection;
        }
    }

    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException var5) {
                LOG.warn("JDBC connection close failed.", var5);
            } finally {
                this.connection = null;
            }
        }
    }

    public Connection reestablishConnection() throws SQLException, ClassNotFoundException {
        this.closeConnection();
        return this.getOrEstablishConnection();
    }

    static {
        DriverManager.getDrivers();
    }
}

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

package org.dinky.metadata.driver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.dinky.metadata.config.AbstractJdbcConfig;
import org.dinky.metadata.convert.AbstractJdbcTypeConvert;
import org.dinky.metadata.query.IDBQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

class AbstractJdbcDriverTest {

    private TestJdbcDriver driver;

    @BeforeEach
    void setUp() {
        driver = new TestJdbcDriver();
        driver.buildDriverConfig(
                "orders",
                "MySql",
                AbstractJdbcConfig.builder()
                        .url("jdbc:mysql://127.0.0.1:3306/orders")
                        .username("root")
                        .password("pwd")
                        .build());
    }

    @AfterEach
    void tearDown() {
        driver.conn.remove();
    }

    @Test
    void createDataSourceKeepsPoolRecoverable() {
        DruidDataSource dataSource = new DruidDataSource();

        driver.createDataSource(dataSource, driver.config.getConnectConfig());

        assertThat(dataSource.isBreakAfterAcquireFailure()).isFalse();
        assertThat(dataSource.isFailFast()).isTrue();
        assertThat(dataSource.isTestWhileIdle()).isTrue();
        assertThat(dataSource.isKeepAlive()).isTrue();
        assertThat(dataSource.getMinEvictableIdleTimeMillis()).isEqualTo(300000L);
        assertThat(dataSource.getMaxEvictableIdleTimeMillis()).isEqualTo(900000L);
        assertThat(dataSource.getValidationQuery()).isEqualTo("select 1");
        assertThat(dataSource.getInitialSize()).isEqualTo(1);
        assertThat(dataSource.getMaxActive()).isEqualTo(8);
        assertThat(dataSource.getMinIdle()).isEqualTo(5);
    }

    @Test
    void isHealthReturnsFalseWhenThreadHasNoConnection() {
        assertThat(driver.isHealth()).isFalse();
    }

    @Test
    void isHealthUsesValidationQueryInsteadOfIsClosed() throws SQLException {
        Connection connection = cachedConnection(true);

        assertThat(driver.isHealth()).isTrue();
        assertThat(driver.conn.get()).isSameAs(connection);
        verify(connection, never()).close();
        verify(connection, never()).isClosed();
    }

    @Test
    void isHealthDropsConnectionClosedByServer() throws SQLException {
        Connection connection = cachedConnection(false);

        assertThat(driver.isHealth()).isFalse();
        assertThat(driver.conn.get()).isNull();
        verify(connection).close();
    }

    @Test
    void connectReusesConnectionThatPassesValidation() throws SQLException {
        Connection connection = cachedConnection(true);

        assertThat(driver.connect()).isSameAs(driver);
        assertThat(driver.conn.get()).isSameAs(connection);
        verify(driver.dataSource, never()).getConnection();
    }

    @Test
    void connectRebuildsConnectionClosedByServer() throws SQLException {
        Connection stale = cachedConnection(false);
        DruidPooledConnection rebuilt = mock(DruidPooledConnection.class);
        when(driver.dataSource.getConnection()).thenReturn(rebuilt);

        assertThat(driver.connect()).isSameAs(driver);
        assertThat(driver.conn.get()).isSameAs(rebuilt);
        verify(stale).close();
        verify(driver.dataSource).getConnection();
    }

    @Test
    void connectRebuildsWhenClosingStaleConnectionFails() throws SQLException {
        Connection stale = cachedConnection(false);
        doThrow(new SQLException("already closed")).when(stale).close();
        DruidPooledConnection rebuilt = mock(DruidPooledConnection.class);
        when(driver.dataSource.getConnection()).thenReturn(rebuilt);

        assertThat(driver.connect()).isSameAs(driver);
        assertThat(driver.conn.get()).isSameAs(rebuilt);
    }

    private Connection cachedConnection(boolean valid) throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement("select 1")).thenReturn(preparedStatement);
        if (valid) {
            when(preparedStatement.executeQuery()).thenReturn(mock(ResultSet.class));
        } else {
            when(preparedStatement.executeQuery()).thenThrow(new SQLException("wait_timeout"));
        }
        driver.conn.set(connection);
        return connection;
    }

    private static class TestJdbcDriver extends AbstractJdbcDriver {

        private final DruidDataSource dataSource = mock(DruidDataSource.class);

        @Override
        public DruidDataSource createDataSource() {
            return dataSource;
        }

        @Override
        String getDriverClass() {
            return "java.lang.String";
        }

        @Override
        public AbstractJdbcTypeConvert getTypeConvert() {
            return null;
        }

        @Override
        public IDBQuery getDBQuery() {
            return null;
        }

        @Override
        public String getType() {
            return "MySql";
        }

        @Override
        public String getName() {
            return "test";
        }
    }
}

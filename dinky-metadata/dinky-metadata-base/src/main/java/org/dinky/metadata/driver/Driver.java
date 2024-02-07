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

import org.dinky.assertion.Asserts;
import org.dinky.data.exception.MetaDataException;
import org.dinky.data.exception.SplitTableException;
import org.dinky.data.model.Column;
import org.dinky.data.model.QueryData;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.metadata.config.AbstractJdbcConfig;
import org.dinky.metadata.config.DriverConfig;
import org.dinky.metadata.enums.DriverType;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.utils.JsonUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Stream;

import cn.hutool.core.text.StrFormatter;

/**
 * Driver
 *
 * @since 2021/7/19 23:15
 */
public interface Driver extends AutoCloseable {

    static Optional<Driver> get(String type) {
        Asserts.checkNotNull(type, "数据源Type配置不能为空");
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        for (Driver driver : drivers) {
            if (driver.canHandle(type)) {
                return Optional.of(driver);
            }
        }
        return Optional.empty();
    }

    static Driver getDriver(String type) {
        synchronized (Driver.class) {
            Optional<Driver> optionalDriver = Driver.get(type);
            if (!optionalDriver.isPresent()) {
                throw new MetaDataException(
                        StrFormatter.format("Missing {} dependency package: dinky-metadata-{}.jar", type, type));
            }
            return optionalDriver.get();
        }
    }

    static Driver build(String name, String type, Map<String, Object> config) {
        if (DriverPool.exist(name)) {
            return getHealthDriver(name);
        }
        Driver driver = getDriver(type).buildDriverConfig(name, type, config).connect();
        DriverPool.push(name, driver);
        return driver;
    }

    static <T> Driver build(DriverConfig<T> config) {
        if (DriverPool.exist(config.getName())) {
            return getHealthDriver(config.getName());
        }
        Driver driver = getDriver(config.getType())
                .buildDriverConfig(config.getName(), config.getType(), config.getConnectConfig())
                .connect();
        DriverPool.push(config.getName(), driver);
        return driver;
    }

    static Driver buildWithOutPool(String name, String type, Map<String, Object> config) {
        Driver driver = getDriver(type);
        return driver.buildDriverConfig(name, type, config).connect();
    }

    static Driver buildUnconnected(String name, String type, Map<String, Object> config) {
        return getDriver(type).buildDriverConfig(name, type, config);
    }

    static Driver getHealthDriver(String key) {
        Driver driver = DriverPool.get(key);
        if (driver.isHealth()) {
            return driver;
        } else {
            return driver.connect();
        }
    }

    static Driver build(String connector, String url, String username, String password) {
        String type = null;
        if (Asserts.isContainsString(connector, "doris")) {
            type = DriverType.DORIS.getValue();
        } else if (Asserts.isEqualsIgnoreCase(connector, "starrocks")) {
            type = DriverType.STARROCKS.getValue();
        } else if (Asserts.isEqualsIgnoreCase(connector, "clickhouse")) {
            type = DriverType.CLICKHOUSE.getValue();
        } else if (Asserts.isEqualsIgnoreCase(connector, "jdbc")) {
            if (url.startsWith("jdbc:mysql")) {
                type = DriverType.MYSQL.getValue();
            } else if (url.startsWith("jdbc:postgresql")) {
                type = DriverType.POSTGRESQL.getValue();
            } else if (url.startsWith("jdbc:oracle")) {
                type = DriverType.ORACLE.getValue();
            } else if (url.startsWith("jdbc:sqlserver")) {
                type = DriverType.SQLSERVER.getValue();
            } else if (url.startsWith("jdbc:phoenix")) {
                type = DriverType.PHOENIX.getValue();
            } else if (url.startsWith("jdbc:pivotal")) {
                type = DriverType.GREENPLUM.getValue();
            }
        }

        if (Asserts.isNull(type)) {
            throw new MetaDataException("Missing DataSource Type:【" + connector + "】");
        }
        AbstractJdbcConfig config = AbstractJdbcConfig.builder()
                .url(url)
                .username(username)
                .password(password)
                .build();
        return build(connector, type, JsonUtils.toMap(config));
    }

    <T> Driver buildDriverConfig(String name, String type, T config);

    boolean canHandle(String type);

    String getType();

    String getName();

    String test();

    boolean isHealth();

    Driver connect();

    @Override
    void close();

    List<Schema> listSchemas();

    boolean existSchema(String schemaName);

    boolean createSchema(String schemaName) throws Exception;

    String generateCreateSchemaSql(String schemaName);

    List<Table> listTables(String schemaName);

    List<Column> listColumns(String schemaName, String tableName);

    List<Column> listColumnsSortByPK(String schemaName, String tableName);

    List<Schema> getSchemasAndTables();

    List<Table> getTablesAndColumns(String schemaName);

    Table getTable(String schemaName, String tableName);

    boolean existTable(Table table);

    boolean createTable(Table table) throws Exception;

    boolean generateCreateTable(Table table) throws Exception;

    boolean dropTable(Table table) throws Exception;

    boolean truncateTable(Table table) throws Exception;

    String getCreateTableSql(Table table);

    String getSqlSelect(Table table);

    String getDropTableSql(Table table);

    String getTruncateTableSql(Table table);

    String generateCreateTableSql(Table table);

    /*
     * boolean insert(Table table, JsonNode data);
     *
     * boolean update(Table table, JsonNode data);
     *
     * boolean delete(Table table, JsonNode data);
     *
     * SelectResult select(String sql);
     */

    boolean execute(String sql) throws Exception;

    int executeUpdate(String sql) throws Exception;

    JdbcSelectResult query(String sql, Integer limit);

    StringBuilder genQueryOption(QueryData queryData);

    JdbcSelectResult executeSql(String sql, Integer limit);

    List<SqlExplainResult> explain(String sql);

    Map<String, String> getFlinkColumnTypeConversion();

    /**
     * 得到分割表
     *
     * @param tableRegList 表正则列表
     * @param splitConfig 分库配置
     * @return {@link Set}<{@link Table}>
     */
    default Set<Table> getSplitTables(List<String> tableRegList, Map<String, String> splitConfig) {
        throw new SplitTableException("目前此数据源不支持分库分表");
    }

    List<Map<String, String>> getSplitSchemaList();

    Stream<JdbcSelectResult> StreamExecuteSql(String statement, Integer maxRowNum);
}

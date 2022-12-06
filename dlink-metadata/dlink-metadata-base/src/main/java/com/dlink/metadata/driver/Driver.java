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

package com.dlink.metadata.driver;

import com.dlink.assertion.Asserts;
import com.dlink.exception.MetaDataException;
import com.dlink.exception.SplitTableException;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.QueryData;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import com.dlink.result.SqlExplainResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * Driver
 *
 * @author wenmo
 * @since 2021/7/19 23:15
 */
public interface Driver extends AutoCloseable {

    static Optional<Driver> get(DriverConfig config) {
        Asserts.checkNotNull(config, "数据源配置不能为空");
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
        for (Driver driver : drivers) {
            if (driver.canHandle(config.getType())) {
                return Optional.of(driver.setDriverConfig(config));
            }
        }
        return Optional.empty();
    }

    static Driver build(DriverConfig config) {
        String key = config.getName();
        if (DriverPool.exist(key)) {
            return getHealthDriver(key);
        }
        synchronized (Driver.class) {
            Optional<Driver> optionalDriver = Driver.get(config);
            if (!optionalDriver.isPresent()) {
                throw new MetaDataException("缺少数据源类型【" + config.getType() + "】的依赖，请在 lib 下添加对应的扩展依赖");
            }
            Driver driver = optionalDriver.get().connect();
            DriverPool.push(key, driver);
            return driver;
        }
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
        if (Asserts.isEqualsIgnoreCase(connector, "doris")) {
            type = "Doris";
        } else if (Asserts.isEqualsIgnoreCase(connector, "starrocks")) {
            type = "StarRocks";
        } else if (Asserts.isEqualsIgnoreCase(connector, "clickhouse")) {
            type = "ClickHouse";
        } else if (Asserts.isEqualsIgnoreCase(connector, "jdbc")) {
            if (url.startsWith("jdbc:mysql")) {
                type = "MySQL";
            } else if (url.startsWith("jdbc:postgresql")) {
                type = "PostgreSql";
            } else if (url.startsWith("jdbc:oracle")) {
                type = "Oracle";
            } else if (url.startsWith("jdbc:sqlserver")) {
                type = "SQLServer";
            } else if (url.startsWith("jdbc:phoenix")) {
                type = "Phoenix";
            } else if (url.startsWith("jdbc:pivotal")) {
                type = "Greenplum";
            }
        }
        if (Asserts.isNull(type)) {
            throw new MetaDataException("缺少数据源类型:【" + connector + "】");
        }
        DriverConfig driverConfig = new DriverConfig(url, type, url, username, password);
        return build(driverConfig);
    }

    Driver setDriverConfig(DriverConfig config);

    boolean canHandle(String type);

    String getType();

    String getName();

    String test();

    boolean isHealth();

    Driver connect();

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

    /* boolean insert(Table table, JsonNode data);

    boolean update(Table table, JsonNode data);

    boolean delete(Table table, JsonNode data);

    SelectResult select(String sql);*/

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
     * @param splitConfig  分库配置
     * @return {@link Set}<{@link Table}>
     */
    default Set<Table> getSplitTables(List<String> tableRegList, Map<String, String> splitConfig) {
        throw new SplitTableException("目前此数据源不支持分库分表");
    }

    ;

    List<Map<String, String>> getSplitSchemaList();
}

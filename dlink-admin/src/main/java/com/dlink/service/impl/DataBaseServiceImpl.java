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

package com.dlink.service.impl;

import com.dlink.assertion.Asserts;
import com.dlink.constant.CommonConstant;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.DataBaseMapper;
import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.DataBase;
import com.dlink.model.Schema;
import com.dlink.model.SqlGeneration;
import com.dlink.model.Table;
import com.dlink.service.DataBaseService;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * DataBaseServiceImpl
 *
 * @author wenmo
 * @since 2021/7/20 23:47
 */
@Service
public class DataBaseServiceImpl extends SuperServiceImpl<DataBaseMapper, DataBase> implements DataBaseService {
    @Override
    public String testConnect(DataBase dataBase) {
        return Driver.build(dataBase.getDriverConfig()).test();
    }

    @Override
    public boolean checkHeartBeat(DataBase dataBase) {
        boolean isHealthy = Asserts.isEquals(CommonConstant.HEALTHY, Driver.build(dataBase.getDriverConfig()).test());
        dataBase.setStatus(isHealthy);
        dataBase.setHeartbeatTime(LocalDateTime.now());
        if (isHealthy) {
            dataBase.setHealthTime(LocalDateTime.now());
        }
        return isHealthy;
    }

    @Override
    public boolean saveOrUpdateDataBase(DataBase dataBase) {
        if (Asserts.isNull(dataBase)) {
            return false;
        }
        if (Asserts.isNull(dataBase.getId())) {
            checkHeartBeat(dataBase);
            return save(dataBase);
        } else {
            DataBase dataBaseInfo = getById(dataBase.getId());
            if (Asserts.isNull(dataBase.getUrl())) {
                dataBase.setUrl(dataBaseInfo.getUrl());
            }
            if (Asserts.isNull(dataBase.getUsername())) {
                dataBase.setUsername(dataBaseInfo.getUsername());
            }
            if (Asserts.isNull(dataBase.getPassword())) {
                dataBase.setPassword(dataBaseInfo.getPassword());
            }
            checkHeartBeat(dataBase);
            return updateById(dataBase);
        }
    }

    @Override
    public List<DataBase> listEnabledAll() {
        return this.list(new QueryWrapper<DataBase>().eq("enabled", 1));
    }

    @Override
    public List<Schema> getSchemasAndTables(Integer id) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase, "该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig());
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        driver.close();
        return schemasAndTables;
    }

    @Override
    public List<Column> listColumns(Integer id, String schemaName, String tableName) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase, "该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig());
        List<Column> columns = driver.listColumns(schemaName, tableName);
        driver.close();
        return columns;
    }

    @Override
    public String getFlinkTableSql(Integer id, String schemaName, String tableName) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase, "该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig());
        List<Column> columns = driver.listColumns(schemaName, tableName);
        Table table = Table.build(tableName, schemaName, columns);
        return table.getFlinkTableSql(dataBase.getName(), dataBase.getFlinkTemplate());
    }

    @Override
    public String getSqlSelect(Integer id, String schemaName, String tableName) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase, "该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig());
        List<Column> columns = driver.listColumns(schemaName, tableName);
        Table table = Table.build(tableName, schemaName, columns);
        return table.getSqlSelect(dataBase.getName());
    }

    @Override
    public String getSqlCreate(Integer id, String schemaName, String tableName) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase, "该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig());
        List<Column> columns = driver.listColumns(schemaName, tableName);
        Table table = Table.build(tableName, schemaName, columns);
        return driver.getCreateTableSql(table);
    }

    @Override
    public JdbcSelectResult queryData(Integer id, String schemaName, String tableName, Integer limit) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase, "该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig());
        JdbcSelectResult result = driver.query("select * from " + schemaName + "." + tableName, limit);
        return result;
    }

    @Override
    public SqlGeneration getSqlGeneration(Integer id, String schemaName, String tableName) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase, "该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig());
        Table table = driver.getTable(schemaName, tableName);
        SqlGeneration sqlGeneration = new SqlGeneration();
        sqlGeneration.setFlinkSqlCreate(table.getFlinkTableSql(dataBase.getName(), dataBase.getFlinkTemplate()));
        sqlGeneration.setSqlSelect(table.getSqlSelect(dataBase.getName()));
        sqlGeneration.setSqlCreate(driver.getCreateTableSql(table));
        driver.close();
        return sqlGeneration;
    }

    @Override
    public List<String> listEnabledFlinkWith() {
        List<DataBase> dataBases = listEnabledAll();
        List<String> list = new ArrayList<>();
        for (DataBase dataBase : dataBases) {
            if (Asserts.isNotNullString(dataBase.getFlinkConfig())) {
                list.add(dataBase.getName() + ":=" + dataBase.getFlinkConfig() + "\n;\n");
            }
        }
        return list;
    }

    @Override
    public String getEnabledFlinkWithSql() {
        List<String> list = listEnabledFlinkWith();
        return StringUtils.join(list, "");
    }
}

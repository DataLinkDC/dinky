package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.constant.CommonConstant;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.DataBaseMapper;
import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.model.*;
import com.dlink.service.DataBaseService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;


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
        boolean isHealthy =  Asserts.isEquals(CommonConstant.HEALTHY,Driver.build(dataBase.getDriverConfig()).test());
        dataBase.setStatus(isHealthy);
        dataBase.setHeartbeatTime(LocalDateTime.now());
        if(isHealthy){
            dataBase.setHealthTime(LocalDateTime.now());
        }
        return isHealthy;
    }

    @Override
    public boolean saveOrUpdateDataBase(DataBase dataBase) {
        if(Asserts.isNull(dataBase)){
            return false;
        }
        if(Asserts.isNull(dataBase.getId())){
            checkHeartBeat(dataBase);
            return save(dataBase);
        }else{
            DataBase dataBaseInfo = getById(dataBase.getId());
            if(Asserts.isNull(dataBase.getUrl())){
                dataBase.setUrl(dataBaseInfo.getUrl());
            }
            if(Asserts.isNull(dataBase.getUsername())){
                dataBase.setUsername(dataBaseInfo.getUsername());
            }
            if(Asserts.isNull(dataBase.getPassword())){
                dataBase.setPassword(dataBaseInfo.getPassword());
            }
            checkHeartBeat(dataBase);
            return updateById(dataBase);
        }
    }

    @Override
    public List<DataBase> listEnabledAll() {
        return this.list(new QueryWrapper<DataBase>().eq("enabled",1));
    }

    @Override
    public List<Schema> getSchemasAndTables(Integer id) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase,"该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig()).connect();
        List<Schema> schemasAndTables = driver.getSchemasAndTables();
        driver.close();
        return schemasAndTables;
    }

    @Override
    public List<Column> listColumns(Integer id, String schemaName, String tableName) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase,"该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig()).connect();
        List<Column> columns = driver.listColumns(schemaName, tableName);
        driver.close();
        return columns;
    }

    @Override
    public String getFlinkTableSql(Integer id, String schemaName, String tableName) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase,"该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig()).connect();
        List<Column> columns = driver.listColumns(schemaName, tableName);
        Table table = Table.build(tableName, schemaName, columns);
        return table.getFlinkTableSql(dataBase.getName(),driver.getFlinkColumnTypeConversion(),dataBase.getFlinkConfig());
    }

    @Override
    public String getSqlSelect(Integer id, String schemaName, String tableName) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase,"该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig()).connect();
        List<Column> columns = driver.listColumns(schemaName, tableName);
        Table table = Table.build(tableName, schemaName, columns);
        return table.getSqlSelect(dataBase.getName());
    }

    @Override
    public String getSqlCreate(Integer id, String schemaName, String tableName) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase,"该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig()).connect();
        List<Column> columns = driver.listColumns(schemaName, tableName);
        Table table = Table.build(tableName, schemaName, columns);
        return driver.getCreateTableSql(table);
    }

    @Override
    public SqlGeneration getSqlGeneration(Integer id, String schemaName, String tableName) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase,"该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig()).connect();
        List<Column> columns = driver.listColumns(schemaName, tableName);
        Table table = Table.build(tableName, schemaName, columns);
        SqlGeneration sqlGeneration = new SqlGeneration();
        sqlGeneration.setFlinkSqlCreate(table.getFlinkTableSql(dataBase.getName(),driver.getFlinkColumnTypeConversion(),dataBase.getFlinkConfig()));
        sqlGeneration.setSqlSelect(table.getSqlSelect(dataBase.getName()));
        sqlGeneration.setSqlCreate(driver.getCreateTableSql(table));
        return sqlGeneration;
    }
}

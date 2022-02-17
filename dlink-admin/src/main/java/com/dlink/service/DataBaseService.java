package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.Column;
import com.dlink.model.DataBase;
import com.dlink.model.Schema;
import com.dlink.model.SqlGeneration;

import java.util.List;

/**
 * DataBaseService
 *
 * @author wenmo
 * @since 2021/7/20 23:47
 */
public interface DataBaseService extends ISuperService<DataBase> {

    String testConnect(DataBase dataBase);

    boolean checkHeartBeat(DataBase dataBase);

    boolean saveOrUpdateDataBase(DataBase dataBase);

    List<DataBase> listEnabledAll();

    List<Schema> getSchemasAndTables(Integer id);

    List<Column> listColumns(Integer id, String schemaName, String tableName);

    String getFlinkTableSql(Integer id, String schemaName, String tableName);

    String getSqlSelect(Integer id, String schemaName, String tableName);

    String getSqlCreate(Integer id, String schemaName, String tableName);

    SqlGeneration getSqlGeneration(Integer id, String schemaName, String tableName);

    List<String> listEnabledFlinkWith();

    String getEnabledFlinkWithSql();
}

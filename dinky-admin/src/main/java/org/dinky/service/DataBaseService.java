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

package org.dinky.service;

import org.dinky.db.service.ISuperService;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.model.Column;
import org.dinky.model.DataBase;
import org.dinky.model.QueryData;
import org.dinky.model.Schema;
import org.dinky.model.SqlGeneration;

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

    JdbcSelectResult queryData(QueryData queryData);

    JdbcSelectResult execSql(QueryData queryData);

    SqlGeneration getSqlGeneration(Integer id, String schemaName, String tableName);

    List<String> listEnabledFlinkWith();

    String getEnabledFlinkWithSql();

    boolean copyDatabase(DataBase database);
}

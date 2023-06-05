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

import org.dinky.data.model.Column;
import org.dinky.data.model.DataBase;
import org.dinky.data.model.QueryData;
import org.dinky.data.model.Schema;
import org.dinky.data.model.SqlGeneration;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

/**
 * DataBaseService
 *
 * @since 2021/7/20 23:47
 */
public interface DataBaseService extends ISuperService<DataBase> {

    /**
     * test connect database
     *
     * @param dataBase {@link DataBase}
     * @return {@link String}
     */
    String testConnect(DataBase dataBase);

    /**
     * check heart beat
     *
     * @param dataBase {@link DataBase}
     * @return {@link Boolean}
     */
    Boolean checkHeartBeat(DataBase dataBase);

    /**
     * save or update database
     *
     * @param dataBase {@link DataBase}
     * @return {@link Boolean}
     */
    Boolean saveOrUpdateDataBase(DataBase dataBase);

    /**
     * enable or disable database
     *
     * @param id {@link Integer}
     * @return {@link Boolean}
     */
    Boolean enable(Integer id);

    /**
     * list all enable database
     *
     * @return {@link List}< {@link DataBase}>
     */
    List<DataBase> listEnabledAll();

    /**
     * get all database of schemas and tables
     *
     * @param id {@link Integer}
     * @return {@link List}< {@link Schema}>
     */
    List<Schema> getSchemasAndTables(Integer id);

    /**
     * get columns of table
     *
     * @param id {@link Integer}
     * @param schemaName {@link String}
     * @param tableName {@link String}
     * @return {@link List}< {@link Column}>
     */
    List<Column> listColumns(Integer id, String schemaName, String tableName);

    String getFlinkTableSql(Integer id, String schemaName, String tableName);

    String getSqlSelect(Integer id, String schemaName, String tableName);

    String getSqlCreate(Integer id, String schemaName, String tableName);

    /**
     * query data of table
     *
     * @param queryData {@link QueryData}
     * @return {@link JdbcSelectResult}
     */
    JdbcSelectResult queryData(QueryData queryData);

    /**
     * exec sql
     *
     * @param queryData {@link QueryData}
     * @return {@link JdbcSelectResult}
     */
    JdbcSelectResult execSql(QueryData queryData);

    /**
     * get sql generation
     *
     * @param id {@link Integer}
     * @param schemaName {@link String}
     * @param tableName {@link String}
     * @return {@link SqlGeneration}
     */
    SqlGeneration getSqlGeneration(Integer id, String schemaName, String tableName);

    List<String> listEnabledFlinkWith();

    String getEnabledFlinkWithSql();

    /**
     * copy database
     *
     * @param database {@link DataBase}
     * @return {@link Boolean}
     */
    Boolean copyDatabase(DataBase database);
}

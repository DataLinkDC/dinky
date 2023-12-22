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

import org.dinky.data.dto.DataBaseDTO;
import org.dinky.data.dto.SqlDTO;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.model.Column;
import org.dinky.data.model.DataBase;
import org.dinky.data.model.QueryData;
import org.dinky.data.model.Schema;
import org.dinky.data.model.SqlGeneration;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.job.JobResult;
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
     * @param dataBaseDTO {@link DataBaseDTO}
     * @return {@link String}
     */
    String testConnect(DataBaseDTO dataBaseDTO);

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
     * @param dataBaseDTO {@link DataBaseDTO}
     * @return {@link Boolean}
     */
    Boolean saveOrUpdateDataBase(DataBaseDTO dataBaseDTO);

    /**
     * enable or disable database
     *
     * @param id {@link Integer}
     * @return {@link Boolean}
     */
    Boolean modifyDataSourceStatus(Integer id);

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

    /**
     * Get the Flink table SQL for the given ID, schema name, and table name.
     *
     * @param id The ID of the Flink table to get the SQL for.
     * @param schemaName The name of the schema for the Flink table.
     * @param tableName The name of the table for the Flink table.
     * @return A string representing the Flink table SQL.
     */
    @Deprecated
    String getFlinkTableSql(Integer id, String schemaName, String tableName);

    /**
     * Get the SQL select statement for the given ID, schema name, and table name.
     *
     * @param id The ID of the table to get the SQL select statement for.
     * @param schemaName The name of the schema for the table.
     * @param tableName The name of the table for the SQL select statement.
     * @return A string representing the SQL select statement.
     */
    @Deprecated
    String getSqlSelect(Integer id, String schemaName, String tableName);

    /**
     * Get the SQL create statement for the given ID, schema name, and table name.
     *
     * @param id The ID of the table to get the SQL create statement for.
     * @param schemaName The name of the schema for the table.
     * @param tableName The name of the table for the SQL create statement.
     * @return A string representing the SQL create statement.
     */
    @Deprecated
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

    /**
     * List all enabled Flink with statements.
     *
     * @return A list of strings representing all enabled Flink with statements.
     */
    List<String> listEnabledFlinkWith();

    /**
     * Get the SQL for enabling Flink with statements.
     *
     * @return A string representing the SQL for enabling Flink with statements.
     */
    String getEnabledFlinkWithSql();

    /**
     * copy database
     *
     * @param dataBaseDTO {@link DataBaseDTO}
     * @return {@link Boolean}
     */
    Boolean copyDatabase(DataBaseDTO dataBaseDTO);

    /**
     * Explain common SQL statements for the given task.
     *
     * @param task A {@link TaskDTO} object representing the task to explain.
     * @return A list of {@link SqlExplainResult} objects representing the explanation results of common SQL statements.
     */
    List<SqlExplainResult> explainCommonSql(TaskDTO task);

    /**
     * Execute the given SQL DTO and return the job result.
     *
     * @param sqlDTO A {@link SqlDTO} object representing the SQL statement to execute.
     * @return A {@link JobResult} object representing the execution result of the SQL statement.
     */
    JobResult executeCommonSql(SqlDTO sqlDTO);

    List<DataBase> selectListByKeyWord(String keyword);

    JobResult StreamExecuteCommonSql(SqlDTO sqlDTO);
}

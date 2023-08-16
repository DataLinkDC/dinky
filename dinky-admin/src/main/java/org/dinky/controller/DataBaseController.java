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

package org.dinky.controller;

import org.dinky.assertion.Asserts;
import org.dinky.data.annotation.Log;
import org.dinky.data.constant.CommonConstant;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Column;
import org.dinky.data.model.DataBase;
import org.dinky.data.model.QueryData;
import org.dinky.data.model.Schema;
import org.dinky.data.model.SqlGeneration;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.metadata.driver.DriverPool;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.service.DataBaseService;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DataBaseController
 *
 * @since 2021/7/20 23:48
 */
@Slf4j
@RestController
@Api(tags = "DataBase Controller")
@RequestMapping("/api/database")
@RequiredArgsConstructor
public class DataBaseController {

    private final DataBaseService databaseService;

    /**
     * save or update database
     *
     * @param database {@link DataBase}
     * @return {@link Result}< {@link Void}>
     */
    @PutMapping
    @Log(title = "Insert Or Update DataBase", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Insert Or Update DataBase")
    public Result<Void> saveOrUpdateDataBase(@RequestBody DataBase database) {
        if (databaseService.saveOrUpdateDataBase(database)) {
            DriverPool.remove(database.getName());
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     * get all database
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult}< {@link DataBase}>
     */
    @PostMapping
    @ApiOperation("DataBase Get All")
    public ProTableResult<DataBase> listDataBases(@RequestBody JsonNode para) {
        final ProTableResult<DataBase> result = databaseService.selectForProTable(para);
        // 密码不返回
        if (result != null && result.getData() != null) {
            for (DataBase data : result.getData()) {
                data.setPassword(null);
            }
        }
        return result;
    }

    /**
     * delete by id
     *
     * @param id {@link Integer}
     * @return {@link Result}< {@link Void}>
     */
    @DeleteMapping("/delete")
    @Log(title = "DataBase Delete By Id", businessType = BusinessType.DELETE)
    @ApiOperation("DataBase Delete By Id")
    public Result<Void> deleteDataBaseById(@RequestParam Integer id) {
        if (databaseService.removeById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        }
        return Result.failed(Status.DELETE_FAILED);
    }

    /**
     * enable or disable by id
     *
     * @param id {@link Integer}
     * @return {@link Result}< {@link Void}>
     */
    @PutMapping("/enable")
    @Log(title = "DataBase Enable Or Disable By Id", businessType = BusinessType.UPDATE)
    @ApiOperation("DataBase Enable Or Disable By Id")
    public Result<Void> modifyDataSourceStatus(@RequestParam Integer id) {
        if (databaseService.modifyDataSourceStatus(id)) {
            return Result.succeed(Status.MODIFY_SUCCESS);
        }
        return Result.failed(Status.MODIFY_FAILED);
    }

    /**
     * get all enabled database
     *
     * @return {@link Result}< {@link List}< {@link DataBase}>>
     */
    @GetMapping("/listEnabledAll")
    @ApiOperation("Get All DataBase Enabled")
    public Result<List<DataBase>> listEnabledAll() {
        List<DataBase> dataBases = databaseService.listEnabledAll();
        return Result.succeed(dataBases);
    }

    /**
     * test connect database
     *
     * @param database {@link DataBase}
     * @return {@link Result}< {@link Void}>
     */
    @PostMapping("/testConnect")
    @Log(title = "DataBase Test Connect", businessType = BusinessType.TEST)
    @ApiOperation("DataBase Test Connect")
    public Result<Void> testConnect(@RequestBody DataBase database) {
        String msg = databaseService.testConnect(database);
        boolean isHealthy = Asserts.isEquals(CommonConstant.HEALTHY, msg);
        if (isHealthy) {
            return Result.succeed(Status.DATASOURCE_CONNECT_SUCCESS);
        } else {
            return Result.failed(msg);
        }
    }

    /**
     * heart beat check by id
     *
     * @param id {@link Integer}
     * @return {@link Result}< {@link Void}>
     */
    @PutMapping("/checkHeartBeatByDataSourceId")
    @Log(title = "DataBase Check Heart Beat By Id", businessType = BusinessType.TEST)
    @ApiOperation("DataBase Check Heart Beat By Id")
    public Result<Void> checkHeartBeatByDataSourceId(@RequestParam Integer id) {
        DataBase dataBase = databaseService.getById(id);
        Asserts.checkNotNull(dataBase, Status.DATASOURCE_NOT_EXIST.getMsg());
        String error = "";
        try {
            databaseService.checkHeartBeat(dataBase);
        } catch (Exception e) {
            error = e.getMessage();
        }
        databaseService.updateById(dataBase);
        if (Asserts.isNotNullString(error)) {
            return Result.failed(error);
        }
        return Result.succeed(Status.DATASOURCE_CONNECT_NORMAL);
    }

    /**
     * get all database of schemas and tables
     *
     * @param id {@link Integer}
     * @return {@link Result}< {@link List}< {@link Schema}>>
     */
    @Cacheable(cacheNames = "metadata_schema", key = "#id")
    @GetMapping("/getSchemasAndTables")
    @ApiOperation("Get All Schemas And Tables")
    public Result<List<Schema>> getSchemasAndTables(@RequestParam Integer id) {
        return Result.succeed(databaseService.getSchemasAndTables(id));
    }

    /**
     * clear cache of schemas and tables
     *
     * @param id {@link Integer}
     * @return {@link Result}< {@link String}>
     */
    @CacheEvict(cacheNames = "metadata_schema", key = "#id")
    @GetMapping("/unCacheSchemasAndTables")
    @ApiOperation("Clear Cache Of Schemas And Tables")
    public Result<String> unCacheSchemasAndTables(@RequestParam Integer id) {
        return Result.succeed(Status.DATASOURCE_CLEAR_CACHE_SUCCESS);
    }

    /**
     * get columns of table
     *
     * @param id {@link Integer}
     * @param schemaName {@link String}
     * @param tableName {@link String}
     * @return {@link Result}< {@link List}< {@link Column}>>
     */
    @GetMapping("/listColumns")
    @ApiOperation("Get Columns Of Table")
    public Result<List<Column>> listColumns(
            @RequestParam Integer id, @RequestParam String schemaName, @RequestParam String tableName) {
        return Result.succeed(databaseService.listColumns(id, schemaName, tableName));
    }

    /**
     * query data of table
     *
     * @param queryData {@link QueryData}
     * @return {@link Result}< {@link JdbcSelectResult}>
     */
    @PostMapping("/queryData")
    @ApiOperation("Query Data Of Table")
    public Result<JdbcSelectResult> queryData(@RequestBody QueryData queryData) {
        JdbcSelectResult jdbcSelectResult = databaseService.queryData(queryData);
        if (jdbcSelectResult.isSuccess()) {
            return Result.succeed(jdbcSelectResult);
        } else {
            return Result.failed();
        }
    }

    /**
     * exec sql
     *
     * @param queryData {@link QueryData}
     * @return {@link Result}< {@link JdbcSelectResult}>
     */
    @PostMapping("/execSql")
    @Log(title = "Exec Sql", businessType = BusinessType.EXECUTE)
    @ApiOperation("Exec Sql")
    public Result<JdbcSelectResult> execSql(@RequestBody QueryData queryData) {
        JdbcSelectResult jdbcSelectResult = databaseService.execSql(queryData);
        if (jdbcSelectResult.isSuccess()) {
            return Result.succeed(jdbcSelectResult);
        } else {
            return Result.failed();
        }
    }

    /**
     * get sql generation
     *
     * @param id {@link Integer}
     * @param schemaName {@link String}
     * @param tableName {@link String}
     * @return {@link Result}< {@link SqlGeneration}>
     */
    @GetMapping("/getSqlGeneration")
    @ApiOperation("Get Sql Generation")
    public Result<SqlGeneration> getSqlGeneration(
            @RequestParam Integer id, @RequestParam String schemaName, @RequestParam String tableName) {
        return Result.succeed(databaseService.getSqlGeneration(id, schemaName, tableName));
    }

    /**
     * copy database
     *
     * @param database {@link DataBase}
     * @return {@link Result}< {@link Void}>
     */
    @PostMapping("/copyDatabase")
    @Log(title = "Copy Database", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Copy Database")
    public Result<Void> copyDatabase(@RequestBody DataBase database) {
        if (databaseService.copyDatabase(database)) {
            return Result.succeed(Status.COPY_SUCCESS);
        } else {
            return Result.failed(Status.COPY_FAILED);
        }
    }
}

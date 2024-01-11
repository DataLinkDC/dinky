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

import cn.uniplore.service.data.datasource.feign.FeignDatasourceProvider;
import cn.uniplore.service.data.datasource.vo.FeignDatasourceByDinkyVO;
import cn.uniplore.service.data.datasource.vo.FeignDatasourceVO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.dinky.assertion.Asserts;
import org.dinky.data.annotations.Log;
import org.dinky.data.constant.CommonConstant;
import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.dto.DataBaseDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Column;
import org.dinky.data.model.DataBase;
import org.dinky.data.model.QueryData;
import org.dinky.data.model.Schema;
import org.dinky.data.model.SqlGeneration;
import org.dinky.data.result.Result;
import org.dinky.metadata.driver.DriverPool;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.service.DataBaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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

    private final FeignDatasourceProvider feignDatasourceProvider;

    /**
     * save or update database
     *
     * @param dataBaseDTO {@link DataBaseDTO}
     * @return {@link Result}< {@link Void}>
     */
    @PutMapping("/saveOrUpdate")
    @Log(title = "Insert Or Update DataBase", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Insert Or Update DataBase")
    @ApiImplicitParam(
            name = "databaseDTO",
            value = "DataBaseDTO",
            required = true,
            dataType = "DataBaseDTO",
            paramType = "body",
            dataTypeClass = DataBaseDTO.class)
    @SaCheckPermission(
            value = {PermissionConstants.REGISTRATION_DATA_SOURCE_ADD, PermissionConstants.REGISTRATION_DATA_SOURCE_EDIT
            },
            mode = SaMode.OR)
    public Result<Void> saveOrUpdateDataBase(@RequestBody DataBaseDTO dataBaseDTO) {
        cn.uniplore.core.common.web.Result<FeignDatasourceByDinkyVO> datasourceVOResult = feignDatasourceProvider.getByIdByDinky(Long.parseLong(dataBaseDTO.getDsDatasourceId()));
        if (datasourceVOResult.getCode() == 200) {
            FeignDatasourceByDinkyVO data = datasourceVOResult.getData();
            Map<String, Object> connectConfig = dataBaseDTO.getConnectConfig();
            JSONObject jsonObject = JSONObject.parseObject(data.getConnectRecipe());
            JSONObject config = jsonObject.getJSONObject("config");
            connectConfig.put("password",config.getString("password"));
        }
        if (databaseService.saveOrUpdateDataBase(dataBaseDTO)) {
            DriverPool.remove(dataBaseDTO.getName());
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     * get all database
     *
     * @param keyword {@link String}
     * @return {@link Result}< {@link DataBase}>
     */
    @GetMapping("/list")
    @ApiOperation("DataBase Get All")
    public Result<List<DataBase>> listDataBases(@RequestParam(value = "keyword") String keyword) {
        List<DataBase> dataBases = new ArrayList<>();
        cn.uniplore.core.common.web.Result<List<FeignDatasourceByDinkyVO>> datasourceAll = feignDatasourceProvider.getDatasourceAllByDinky(null, keyword, null, keyword);
        if(datasourceAll.getCode() == 200){
            List<FeignDatasourceByDinkyVO> data = datasourceAll.getData();
            for (FeignDatasourceByDinkyVO vo:data){
                DataBase dataBase = new DataBase().toDataBaseByDataEntityByDinky(vo);
                DataBase selectOne = databaseService.getBaseMapper().selectOne(new QueryWrapper<DataBase>().eq("enabled", 1).eq("ds_datasource_id", dataBase.getDsDatasourceId()));
                if(selectOne != null){
                    dataBase.setStatus(selectOne.getStatus());
                    dataBase.setFlinkConfig(selectOne.getFlinkConfig());
                    dataBase.setFlinkTemplate(selectOne.getFlinkTemplate());
                    dataBase.setId(selectOne.getId());
                }
                Map<String, Object> connectConfig = dataBase.getConnectConfig();
                connectConfig.put("password","******");
                dataBases.add(dataBase);
            }
        }
        return Result.succeed(dataBases);
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
    @ApiImplicitParam(
            name = "id",
            value = "DataBase Id",
            required = true,
            dataType = "Integer",
            paramType = "path",
            dataTypeClass = Integer.class)
    @SaCheckPermission(PermissionConstants.REGISTRATION_DATA_SOURCE_DELETE)
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
    @ApiImplicitParam(
            name = "id",
            value = "DataBase Id",
            required = true,
            dataType = "Integer",
            paramType = "path",
            dataTypeClass = Integer.class)
    @SaCheckPermission(PermissionConstants.REGISTRATION_DATA_SOURCE_EDIT)
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
        cn.uniplore.core.common.web.Result<List<FeignDatasourceByDinkyVO>> datasourceAll = feignDatasourceProvider.getDatasourceAllByDinky(null, null, null, null);
        List<DataBase> dataBases = new ArrayList<>();
        if(datasourceAll.getCode() == 200){
            List<FeignDatasourceByDinkyVO> data = datasourceAll.getData();
            for (FeignDatasourceByDinkyVO vo:data){
                DataBase dataBase = new DataBase().toDataBaseByDataEntityByDinky(vo);
                DataBase selectOne = databaseService.getBaseMapper().selectOne(new QueryWrapper<DataBase>().eq("enabled", 1).eq("ds_datasource_id", dataBase.getDsDatasourceId()));
                if(selectOne != null){
                    dataBase.setStatus(selectOne.getStatus());
                    dataBase.setFlinkConfig(selectOne.getFlinkConfig());
                    dataBase.setFlinkTemplate(selectOne.getFlinkTemplate());
                    dataBase.setId(selectOne.getId());
                }
                Map<String, Object> connectConfig = dataBase.getConnectConfig();
                connectConfig.put("password","******");
                dataBases.add(dataBase);
            }
        }
//        List<DataBase> dataBases2 = databaseService.listEnabledAll();
        return Result.succeed(dataBases);
    }

    /**
     * test connect database
     *
     * @param dataBaseDTO {@link DataBaseDTO}
     * @return {@link Result}< {@link Void}>
     */
    @PostMapping("/testConnect")
    @Log(title = "DataBase Test Connect", businessType = BusinessType.TEST)
    @ApiOperation("DataBase Test Connect")
    @ApiImplicitParam(
            name = "dataBaseDTO",
            value = "DataBaseDTO",
            required = true,
            dataType = "DataBaseDTO",
            paramType = "body",
            dataTypeClass = DataBaseDTO.class)
    public Result<Void> testConnect(@RequestBody DataBaseDTO dataBaseDTO) {
        String msg = databaseService.testConnect(dataBaseDTO);
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
    @ApiImplicitParam(
            name = "id",
            value = "DataBase Id",
            required = true,
            dataType = "Long",
            paramType = "path",
            dataTypeClass = Integer.class,
            example = "1")
    @SaCheckPermission(PermissionConstants.REGISTRATION_DATA_SOURCE_CHECK_HEARTBEAT)
    public Result<Void> checkHeartBeatByDataSourceId(@RequestParam Long id) {
        cn.uniplore.core.common.web.Result<FeignDatasourceByDinkyVO> datasourceVOResult = feignDatasourceProvider.getByIdByDinky(id);
        if(datasourceVOResult.getCode() == 200){
            FeignDatasourceByDinkyVO data = datasourceVOResult.getData();
            DataBase dataBase = new DataBase().toDataBaseByDataEntityByDinky(data);
            Asserts.checkNotNull(dataBase, Status.DATASOURCE_NOT_EXIST.getMessage());
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
        return Result.failed(datasourceVOResult.getMsg());
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
    @ApiImplicitParam(
            name = "id",
            value = "DataBase Id",
            required = true,
            dataType = "Integer",
            paramType = "path",
            dataTypeClass = Integer.class,
            example = "1")
    public Result<List<Schema>> getSchemasAndTables(@RequestParam Long id) {
        cn.uniplore.core.common.web.Result<FeignDatasourceByDinkyVO> datasourceVOResult = feignDatasourceProvider.getByIdByDinky(id);
        if(datasourceVOResult.getCode() == 200) {
            FeignDatasourceByDinkyVO data = datasourceVOResult.getData();
            DataBase dataBase = new DataBase().toDataBaseByDataEntityByDinky(data);
            DataBase selectOne = databaseService.getBaseMapper().selectOne(new QueryWrapper<DataBase>().eq("enabled", 1).eq("ds_datasource_id", dataBase.getDsDatasourceId()));
            if(selectOne != null){
                dataBase.setStatus(selectOne.getStatus());
                dataBase.setFlinkConfig(selectOne.getFlinkConfig());
                dataBase.setFlinkTemplate(selectOne.getFlinkTemplate());
                dataBase.setId(selectOne.getId());
            }
            return Result.succeed(databaseService.getSchemasAndTables(dataBase));
        }
       return Result.failed(datasourceVOResult.getMsg());
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
    @ApiImplicitParam(
            name = "id",
            value = "DataBase Id",
            required = true,
            dataType = "Integer",
            paramType = "path",
            dataTypeClass = Integer.class,
            example = "1")
    public Result<String> unCacheSchemasAndTables(@RequestParam Integer id) {
        return Result.succeed(Status.DATASOURCE_CLEAR_CACHE_SUCCESS);
    }

    /**
     * get columns of table
     *
     * @param id         {@link Integer}
     * @param schemaName {@link String}
     * @param tableName  {@link String}
     * @return {@link Result}< {@link List}< {@link Column}>>
     */
    @GetMapping("/listColumns")
    @ApiOperation("Get Columns Of Table")
    @ApiImplicitParams(
            value = {
                @ApiImplicitParam(
                        name = "id",
                        value = "DataBase Id",
                        required = true,
                        dataType = "Integer",
                        paramType = "path",
                        dataTypeClass = Integer.class,
                        example = "1"),
                @ApiImplicitParam(
                        name = "schemaName",
                        value = "Schema Name",
                        required = true,
                        dataType = "String",
                        paramType = "query",
                        dataTypeClass = String.class,
                        example = "public"),
                @ApiImplicitParam(
                        name = "tableName",
                        value = "Table Name",
                        required = true,
                        dataType = "String",
                        paramType = "query",
                        dataTypeClass = String.class,
                        example = "user")
            })
    public Result<List<Column>> listColumns(
            @RequestParam Long id, @RequestParam String schemaName, @RequestParam String tableName) {
        cn.uniplore.core.common.web.Result<FeignDatasourceByDinkyVO> datasourceVOResult = feignDatasourceProvider.getByIdByDinky(id);
        if(datasourceVOResult.getCode() == 200) {
            FeignDatasourceByDinkyVO data = datasourceVOResult.getData();
            DataBase dataBase = new DataBase().toDataBaseByDataEntityByDinky(data);
            DataBase selectOne = databaseService.getBaseMapper().selectOne(new QueryWrapper<DataBase>().eq("enabled", 1).eq("ds_datasource_id", dataBase.getDsDatasourceId()));
            if (selectOne != null) {
                dataBase.setStatus(selectOne.getStatus());
                dataBase.setFlinkConfig(selectOne.getFlinkConfig());
                dataBase.setFlinkTemplate(selectOne.getFlinkTemplate());
                dataBase.setId(selectOne.getId());
            }
            return Result.succeed(databaseService.listColumns(dataBase, schemaName, tableName));
        }
        return Result.failed(datasourceVOResult.getMsg());
    }

    /**
     * query data of table
     *
     * @param queryData {@link QueryData}
     * @return {@link Result}< {@link JdbcSelectResult}>
     */
    @PostMapping("/queryData")
    @ApiOperation("Query Data Of Table")
    @ApiImplicitParam(
            name = "queryData",
            value = "Query Data",
            required = true,
            dataType = "QueryData",
            paramType = "body",
            dataTypeClass = QueryData.class)
    public Result<JdbcSelectResult> queryData(@RequestBody QueryData queryData) {
        cn.uniplore.core.common.web.Result<FeignDatasourceByDinkyVO> datasourceVOResult = feignDatasourceProvider.getByIdByDinky(queryData.getId());
        if(datasourceVOResult.getCode() == 200) {
            FeignDatasourceByDinkyVO data = datasourceVOResult.getData();
            DataBase dataBase = new DataBase().toDataBaseByDataEntityByDinky(data);
            DataBase selectOne = databaseService.getBaseMapper().selectOne(new QueryWrapper<DataBase>().eq("enabled", 1).eq("ds_datasource_id", dataBase.getDsDatasourceId()));
            if (selectOne != null) {
                dataBase.setStatus(selectOne.getStatus());
                dataBase.setFlinkConfig(selectOne.getFlinkConfig());
                dataBase.setFlinkTemplate(selectOne.getFlinkTemplate());
                dataBase.setId(selectOne.getId());
            }
            JdbcSelectResult jdbcSelectResult = databaseService.queryData(queryData,dataBase);
            if (jdbcSelectResult.isSuccess()) {
                return Result.succeed(jdbcSelectResult);
            } else {
                return Result.failed();
            }
        }
        return Result.failed(datasourceVOResult.getMsg());

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
    @ApiImplicitParam(
            name = "queryData",
            value = "Query Data",
            required = true,
            dataType = "QueryData",
            paramType = "body",
            dataTypeClass = QueryData.class)
    @SaCheckPermission(PermissionConstants.REGISTRATION_DATA_SOURCE_EXEC_SQL)
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
     * @param id         {@link Integer}
     * @param schemaName {@link String}
     * @param tableName  {@link String}
     * @return {@link Result}< {@link SqlGeneration}>
     */
    @GetMapping("/getSqlGeneration")
    @ApiOperation("Get Sql Generation")
    @ApiImplicitParams(
            value = {
                @ApiImplicitParam(
                        name = "id",
                        value = "DataBase Id",
                        required = true,
                        dataType = "Integer",
                        paramType = "path",
                        dataTypeClass = Integer.class,
                        example = "1"),
                @ApiImplicitParam(
                        name = "schemaName",
                        value = "Schema Name",
                        required = true,
                        dataType = "String",
                        paramType = "query",
                        dataTypeClass = String.class,
                        example = "public"),
                @ApiImplicitParam(
                        name = "tableName",
                        value = "Table Name",
                        required = true,
                        dataType = "String",
                        paramType = "query",
                        dataTypeClass = String.class,
                        example = "user")
            })
    public Result<SqlGeneration> getSqlGeneration(
            @RequestParam Long id, @RequestParam String schemaName, @RequestParam String tableName) {
        cn.uniplore.core.common.web.Result<FeignDatasourceByDinkyVO> datasourceVOResult = feignDatasourceProvider.getByIdByDinky(id);
        if(datasourceVOResult.getCode() == 200) {
            FeignDatasourceByDinkyVO data = datasourceVOResult.getData();
            DataBase dataBase = new DataBase().toDataBaseByDataEntityByDinky(data);
            DataBase selectOne = databaseService.getBaseMapper().selectOne(new QueryWrapper<DataBase>().eq("enabled", 1).eq("ds_datasource_id", dataBase.getDsDatasourceId()));
            if (selectOne != null) {
                dataBase.setStatus(selectOne.getStatus());
                dataBase.setFlinkConfig(selectOne.getFlinkConfig());
                dataBase.setFlinkTemplate(selectOne.getFlinkTemplate());
                dataBase.setId(selectOne.getId());
            }
            return Result.succeed(databaseService.getSqlGeneration(dataBase, schemaName, tableName));
        }
        return Result.failed(datasourceVOResult.getMsg());

    }

    /**
     * copy database
     *
     * @param dataBaseDTO {@link DataBaseDTO}
     * @return {@link Result}< {@link Void}>
     */
    @PostMapping("/copyDatabase")
    @Log(title = "Copy Database", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Copy Database")
    @SaCheckPermission(PermissionConstants.REGISTRATION_DATA_SOURCE_COPY)
    public Result<Void> copyDatabase(@RequestBody DataBaseDTO dataBaseDTO) {
        if (databaseService.copyDatabase(dataBaseDTO)) {
            return Result.succeed(Status.COPY_SUCCESS);
        } else {
            return Result.failed(Status.COPY_FAILED);
        }
    }
}

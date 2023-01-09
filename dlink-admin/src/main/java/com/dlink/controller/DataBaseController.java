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

package com.dlink.controller;

import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.constant.CommonConstant;
import com.dlink.metadata.driver.DriverPool;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.DataBase;
import com.dlink.model.QueryData;
import com.dlink.service.DataBaseService;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import lombok.extern.slf4j.Slf4j;

/**
 * DataBaseController
 *
 * @author wenmo
 * @since 2021/7/20 23:48
 */
@Slf4j
@RestController
@RequestMapping("/api/database")
public class DataBaseController {

    @Autowired
    private DataBaseService databaseService;
    private static Logger logger = LoggerFactory.getLogger(DataBaseController.class);

    /**
     * 新增或者更新
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody DataBase database) {
        if (databaseService.saveOrUpdateDataBase(database)) {
            DriverPool.remove(database.getName());
            return Result.succeed("更新成功");
        } else {
            return Result.failed("更新失败");
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<DataBase> listDataBases(@RequestBody JsonNode para) {
        return databaseService.selectForProTable(para);
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    public Result deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!databaseService.removeById(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error.toString() + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * 获取指定ID的信息
     */
    @PostMapping("/getOneById")
    public Result getOneById(@RequestBody DataBase database) {
        database = databaseService.getById(database.getId());
        return Result.succeed(database, "获取成功");
    }

    /**
     * 获取可用的数据库列表
     */
    @GetMapping("/listEnabledAll")
    public Result listEnabledAll() {
        List<DataBase> dataBases = databaseService.listEnabledAll();
        return Result.succeed(dataBases, "获取成功");
    }

    /**
     * 连接测试
     */
    @PostMapping("/testConnect")
    public Result testConnect(@RequestBody DataBase database) {
        String msg = databaseService.testConnect(database);
        boolean isHealthy = Asserts.isEquals(CommonConstant.HEALTHY, msg);
        if (isHealthy) {
            return Result.succeed("数据源连接测试成功!");
        } else {
            return Result.failed(msg);
        }
    }

    /**
     * 全部心跳监测
     */
    @PostMapping("/checkHeartBeats")
    public Result checkHeartBeats() {
        List<DataBase> dataBases = databaseService.listEnabledAll();
        for (int i = 0; i < dataBases.size(); i++) {
            DataBase dataBase = dataBases.get(i);
            databaseService.checkHeartBeat(dataBase);
            databaseService.updateById(dataBase);
        }
        return Result.succeed("状态刷新完成");
    }

    /**
     * 心跳检测指定ID
     */
    @GetMapping("/checkHeartBeatById")
    public Result checkHeartBeatById(@RequestParam Integer id) {
        DataBase dataBase = databaseService.getById(id);
        Asserts.checkNotNull(dataBase, "该数据源不存在！");
        databaseService.checkHeartBeat(dataBase);
        databaseService.updateById(dataBase);
        return Result.succeed(dataBase, "状态刷新完成");
    }

    /**
     * 获取元数据的表
     */
    @Cacheable(cacheNames = "metadata_schema", key = "#id")
    @GetMapping("/getSchemasAndTables")
    public Result getSchemasAndTables(@RequestParam Integer id) {
        return Result.succeed(databaseService.getSchemasAndTables(id), "获取成功");
    }

    /**
     * 清除元数据表的缓存
     */
    @CacheEvict(cacheNames = "metadata_schema", key = "#id")
    @GetMapping("/unCacheSchemasAndTables")
    public Result unCacheSchemasAndTables(@RequestParam Integer id) {
        return Result.succeed("clear cache", "success");
    }

    /**
     * 获取元数据的指定表的列
     */
    @GetMapping("/listColumns")
    public Result listColumns(@RequestParam Integer id, @RequestParam String schemaName,
            @RequestParam String tableName) {
        return Result.succeed(databaseService.listColumns(id, schemaName, tableName), "获取成功");
    }

    /**
     * 获取元数据的指定表的数据
     */
    @PostMapping("/queryData")
    public Result queryData(@RequestBody QueryData queryData) {
        JdbcSelectResult jdbcSelectResult = databaseService.queryData(queryData);
        if (jdbcSelectResult.isSuccess()) {
            return Result.succeed(jdbcSelectResult, "获取成功");
        } else {
            return Result.failed(jdbcSelectResult, "查询失败");
        }
    }

    /**
     * 执行sql
     */
    @PostMapping("/execSql")
    public Result execSql(@RequestBody QueryData queryData) {
        JdbcSelectResult jdbcSelectResult = databaseService.execSql(queryData);
        if (jdbcSelectResult.isSuccess()) {
            return Result.succeed(jdbcSelectResult, "获取成功");
        } else {
            return Result.failed(jdbcSelectResult, "查询失败");
        }
    }

    /**
     * 获取 SqlGeneration
     */
    @GetMapping("/getSqlGeneration")
    public Result getSqlGeneration(@RequestParam Integer id, @RequestParam String schemaName,
            @RequestParam String tableName) {
        return Result.succeed(databaseService.getSqlGeneration(id, schemaName, tableName), "获取成功");
    }

    /**
     * copyDatabase
     */
    @PostMapping("/copyDatabase")
    public Result copyDatabase(@RequestBody DataBase database) {
        if (databaseService.copyDatabase(database)) {
            return Result.succeed("复制成功!");
        } else {
            return Result.failed("复制失败！");
        }
    }
}

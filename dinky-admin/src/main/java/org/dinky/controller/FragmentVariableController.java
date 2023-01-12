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

import org.dinky.common.result.ProTableResult;
import org.dinky.common.result.Result;
import org.dinky.model.FragmentVariable;
import org.dinky.service.FragmentVariableService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

/**
 * FragmentVariableController
 *
 * @author zhumingye
 * @since 2022/8/18
 */
@Slf4j
@RestController
@RequestMapping("/api/fragment")
public class FragmentVariableController {

    @Autowired private FragmentVariableService fragmentVariableService;

    /** 新增或者更新 */
    @PutMapping
    public Result saveOrUpdate(@RequestBody FragmentVariable fragmentVariable) throws Exception {
        if (fragmentVariableService.saveOrUpdate(fragmentVariable)) {
            return Result.succeed("保存成功");
        } else {
            return Result.failed("保存失败");
        }
    }

    /** 动态查询列表 */
    @PostMapping
    public ProTableResult<FragmentVariable> listFragmentVariable(@RequestBody JsonNode para) {
        return fragmentVariableService.selectForProTable(para);
    }

    /** 批量删除 */
    @DeleteMapping
    public Result deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!fragmentVariableService.removeById(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed(
                        "删除部分成功，但" + error.toString() + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }
}

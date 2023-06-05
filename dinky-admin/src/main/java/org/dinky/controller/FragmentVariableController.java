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

import org.dinky.data.model.FragmentVariable;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.FragmentVariableService;
import org.dinky.utils.I18nMsgUtils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** FragmentVariableController */
@Slf4j
@RestController
@RequestMapping("/api/fragment")
@RequiredArgsConstructor
public class FragmentVariableController {

    private final FragmentVariableService fragmentVariableService;

    /**
     * save or update
     *
     * @param fragmentVariable {@link FragmentVariable}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping
    public Result<Void> saveOrUpdate(@RequestBody FragmentVariable fragmentVariable)
            throws Exception {
        if (fragmentVariableService.saveOrUpdate(fragmentVariable)) {
            return Result.succeed(I18nMsgUtils.getMsg("save.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("save.failed"));
        }
    }

    /**
     * query list
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} of {@link FragmentVariable}
     */
    @PostMapping
    public ProTableResult<FragmentVariable> listFragmentVariable(@RequestBody JsonNode para) {
        return fragmentVariableService.selectForProTable(para);
    }

    /**
     * delete by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteById(@RequestParam Integer id) {
        if (fragmentVariableService.removeById(id)) {
            return Result.succeed(I18nMsgUtils.getMsg("delete.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("delete.failed"));
        }
    }

    /**
     * enable or disable
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping("/enable")
    public Result<Void> enable(@RequestParam Integer id) {
        if (fragmentVariableService.enable(id)) {
            return Result.succeed(I18nMsgUtils.getMsg("save.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("save.failed"));
        }
    }

    /**
     * batch delete , this method is deprecated, please use {@link #deleteById(Integer)}
     *
     * @param para {@link JsonNode}
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping
    @Deprecated
    public Result<Void> deleteMul(@RequestBody JsonNode para) {
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
                return Result.succeed("删除部分成功，但" + error + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }
}

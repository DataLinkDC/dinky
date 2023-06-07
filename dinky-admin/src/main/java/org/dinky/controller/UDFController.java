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

import org.dinky.data.model.UDFTemplate;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.UDFTemplateService;
import org.dinky.utils.I18nMsgUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** UDFController */
@Slf4j
@RestController
@RequestMapping("/api/udf/template")
@RequiredArgsConstructor
public class UDFController {

    private final UDFTemplateService udfTemplateService;

    @PostMapping("/tree")
    public Result<List<Object>> listUdfTemplates() {
        List<UDFTemplate> list = udfTemplateService.list();
        Map<String, Dict> one = new HashMap<>(3);
        Map<String, Dict> two = new HashMap<>(3);
        Map<String, Dict> three = new HashMap<>(3);
        Map<String, Object> result = new HashMap<>(3);
        list.forEach(
                t -> {
                    one.putIfAbsent(
                            t.getCodeType(),
                            Dict.create()
                                    .set("label", t.getCodeType())
                                    .set("value", t.getCodeType()));
                    two.putIfAbsent(
                            t.getCodeType() + t.getFunctionType(),
                            Dict.create()
                                    .set("label", t.getFunctionType())
                                    .set("value", t.getFunctionType()));
                    three.putIfAbsent(
                            t.getCodeType() + t.getFunctionType() + t.getId(),
                            Dict.create().set("label", t.getName()).set("value", t.getId()));
                });
        Set<String> twoKeys = two.keySet();
        Set<String> threeKeys = three.keySet();
        one.forEach(
                (k1, v1) -> {
                    result.put(k1, v1);
                    ArrayList<Dict> c1 = new ArrayList<>();
                    v1.put("children", c1);
                    twoKeys.stream()
                            .filter(x -> x.contains(k1))
                            .map(x -> StrUtil.strip(x, k1))
                            .forEach(
                                    k2 -> {
                                        Dict v2 = two.get(k1 + k2);
                                        c1.add(v2);
                                        ArrayList<Dict> c2 = new ArrayList<>();
                                        v2.put("children", c2);
                                        threeKeys.stream()
                                                .filter(x -> x.contains(k1 + k2))
                                                .map(x -> StrUtil.strip(x, k1 + k2))
                                                .forEach(
                                                        k3 -> {
                                                            c2.add(three.get(k1 + k2 + k3));
                                                        });
                                    });
                });
        return Result.succeed(CollUtil.newArrayList(result.values()));
    }

    /**
     * get udf template list
     *
     * @param params {@link JsonNode}
     * @return {@link ProTableResult} <{@link UDFTemplate}>
     */
    @PostMapping("/list")
    public ProTableResult<UDFTemplate> listUdfTemplates(@RequestBody JsonNode params) {
        return udfTemplateService.selectForProTable(params);
    }

    /**
     * save or update udf template
     *
     * @param udfTemplate {@link UDFTemplate}
     * @return {@link Result} <{@link String}>
     */
    @PutMapping
    public Result<String> saveOrUpdate(@RequestBody UDFTemplate udfTemplate) {
        return udfTemplateService.saveOrUpdate(udfTemplate)
                ? Result.succeed(I18nMsgUtils.getMsg("save.success"))
                : Result.failed(I18nMsgUtils.getMsg("save.failed"));
    }

    /**
     * delete udf template by id, this method is deprecated, please use {@link #delete(Integer id)}
     *
     * @param para {@link JsonNode}
     * @return {@link Result} <{@link String}>
     */
    @DeleteMapping("/template/list")
    @Deprecated
    public Result deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!udfTemplateService.removeById(id)) {
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

    /**
     * delete udf template by id
     *
     * @param id {@link Integer}
     * @return {@link Result} <{@link Void}>
     */
    @DeleteMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@RequestParam Integer id) {
        if (udfTemplateService.removeById(id)) {
            return Result.succeed(I18nMsgUtils.getMsg("delete.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("delete.failed"));
        }
    }

    @PutMapping("/enable")
    public Result<Void> enable(@RequestParam Integer id) {
        if (udfTemplateService.enable(id)) {
            return Result.succeed(I18nMsgUtils.getMsg("modify.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("modify.failed"));
        }
    }
}

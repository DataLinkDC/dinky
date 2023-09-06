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

import org.dinky.data.enums.Status;
import org.dinky.data.model.AlertRule;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.AlertRuleService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/alertRule")
public class AlertRuleController {

    private final AlertRuleService alertRuleService;

    @PostMapping("/list")
    public ProTableResult<AlertRule> list(@RequestBody JsonNode para) {
        ProTableResult<AlertRule> result = alertRuleService.selectForProTable(para);
        //The reason for this is to deal with internationalization
        List<AlertRule> list = result.getData()
                .stream()
                .peek(rule -> rule.setName(Status.findMessageByKey(rule.getName())))
                .peek(rule -> rule.setDescription(Status.findMessageByKey(rule.getDescription())))
                .collect(Collectors.toList());
        result.setData(list);
        return result;
    }

    @PutMapping
    public Result<Boolean> put(@RequestBody AlertRule alertRule) {
        return Result.succeed(alertRuleService.saveOrUpdate(alertRule));
    }

    @DeleteMapping
    public Result<Boolean> delete(int id) {
        return Result.succeed(alertRuleService.removeById(id));
    }
}

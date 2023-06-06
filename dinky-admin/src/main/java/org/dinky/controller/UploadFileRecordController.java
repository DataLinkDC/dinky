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

import org.dinky.data.model.UploadFileRecord;
import org.dinky.data.result.Result;
import org.dinky.service.UploadFileRecordService;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/** FileUploadController */
@Slf4j
@RestController
@RequestMapping("/api/uploadFileRecord")
public class UploadFileRecordController {

    @Resource private UploadFileRecordService uploadFileRecordService;

    /** @param record {@link UploadFileRecord} */
    @PostMapping("/list")
    public Result<String> get(@RequestBody UploadFileRecord record) {
        List<UploadFileRecord> records = uploadFileRecordService.list(new QueryWrapper<>(record));
        return Result.succeed(JSONUtil.toJsonStr(records));
    }
}

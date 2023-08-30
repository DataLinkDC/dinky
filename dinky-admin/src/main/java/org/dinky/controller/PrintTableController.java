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

import org.dinky.data.result.Result;
import org.dinky.service.PrintTableService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

@RestController
@Api(tags = "Print Table Controller")
@AllArgsConstructor
@RequestMapping("/api")
public class PrintTableController {

    private final PrintTableService printTableService;

    @GetMapping(value = "/subscribe/print", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation("Subscribe p table")
    public SseEmitter subscribe(@RequestParam String table) {
        return printTableService.registerListenEntry(table);
    }

    @PutMapping("/unSubscribe/print")
    @ApiOperation("UnSubscribe print table")
    public Result<Void> unsubscribe(@RequestParam String table) {
        printTableService.unRegisterListenEntry(table);
        return Result.succeed();
    }
}

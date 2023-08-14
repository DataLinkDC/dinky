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

import io.swagger.annotations.ApiOperation;
import org.dinky.data.result.Result;
import org.dinky.service.WatchTableService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class WatchTableController {

    private final WatchTableService watchTableService;

    @GetMapping(name = "/subscribe/watch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation("Subscribe watch table")
    public SseEmitter subscribe(@RequestParam Integer id, @RequestParam String table) {
        return watchTableService.registerListenEntry(id, table);
    }

    @PutMapping("/unSubscribe/watch")
    @ApiOperation("UnSubscribe watch table")
    public Result<Void> unsubscribe(@RequestParam Integer id, @RequestParam String table) {
        watchTableService.unRegisterListenEntry(id, table);
        return Result.succeed();
    }
}

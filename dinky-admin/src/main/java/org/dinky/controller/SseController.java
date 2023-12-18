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

import org.dinky.context.SseSessionContextHolder;
import org.dinky.data.dto.SseSubscribeDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.result.Result;

import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@Api(tags = "SSE Controller")
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    @PostMapping(value = "/subscribeTopic")
    @ApiOperation("subscribeTopic")
    @ApiImplicitParam(name = "topics", value = "topics", required = true, dataType = "List")
    public Result<Set<String>> subscribeTopic(@RequestBody SseSubscribeDTO subscribeDTO) {
        Set<String> b = SseSessionContextHolder.subscribeTopic(subscribeDTO.getSessionKey(), subscribeDTO.getTopics());
        return Result.succeed(b, Status.SUCCESS);
    }

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation("Connect Sse")
    @ApiImplicitParam(name = "sessionKey", value = "Session unique key", required = true, dataType = "String")
    public SseEmitter connect(String sessionKey) {
        return SseSessionContextHolder.connectSession(sessionKey);
    }
}

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

import org.dinky.data.annotation.PublicInterface;
import org.dinky.service.MonitorService;
import org.dinky.sse.SseEmitterUTF8;

import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Opt;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {
    private final MonitorService monitorService;

    @GetMapping(value = "/getLastUpdateData", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin("*")
    @PublicInterface
    public SseEmitter getLastUpdateData(Long lastTime) {
        SseEmitter emitter = new SseEmitterUTF8(TimeUnit.MINUTES.toMillis(30));
        return monitorService.sendLatestData(
                emitter, DateUtil.date(Opt.ofNullable(lastTime).orElse(DateUtil.date().getTime())));
    }
}

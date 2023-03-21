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

import org.dinky.service.WatchTableService;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class WatchTableController {

    private final WatchTableService watchTableService;

    @MessageMapping("/broadcast")
    public String broadcast(String message) {
        return "back" + message;
    }

    @PutMapping("/subscribe/{id}/{table}")
    public String subscribe(@PathVariable Integer id, @PathVariable String table) {
        watchTableService.registerListenEntry(id, table);
        return "successful";
    }

    @PutMapping("/unsubscribe/{id}/{table}")
    public String unsubscribe(@PathVariable Integer id, @PathVariable String table) {
        watchTableService.unRegisterListenEntry(id, table);
        return "successful";
    }

    @MessageMapping("/one")
    public void one(String message, Principal principal) {}
}

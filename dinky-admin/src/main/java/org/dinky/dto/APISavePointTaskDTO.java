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

package com.dlink.dto;

import com.dlink.gateway.config.SavePointType;

import lombok.Getter;
import lombok.Setter;

/**
 * APISavePointTaskDTO
 *
 * @author wenmo
 * @since 2022/03/25 19:05
 */
@Getter
@Setter
public class APISavePointTaskDTO {
    private Integer taskId;
    private String type = SavePointType.TRIGGER.getValue();

}

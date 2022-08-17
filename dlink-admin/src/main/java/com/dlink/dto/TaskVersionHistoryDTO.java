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

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author huang
 * @description: 任务版本记录
 */
@Data
public class TaskVersionHistoryDTO implements Serializable {
    private Integer id;
    private Integer taskId;
    private String name;
    private String alias;
    private String dialect;
    private String type;
    private String statement;
    private Integer versionId;
    private Date createTime;
}

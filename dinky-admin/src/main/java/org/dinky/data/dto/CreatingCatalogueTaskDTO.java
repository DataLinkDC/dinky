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

package org.dinky.data.dto;

import org.dinky.scheduler.model.DinkyTaskRequest;

import java.util.List;

import lombok.Data;

@Data
public class CreatingCatalogueTaskDTO {
    /**
     * 目录名称列表
     * 例子：["catalogue1", "catalogue2"]
     * 数组第 0 个元素为根目录，依次下一个元素是上一个元素的子目录
     * 目录不存在会新建，已存在就保持原来的目录
     */
    private List<String> catalogueNames;
    /**
     * 作业类型：FlinkSql、Mysql 等，详见 dinky 创建作业时的作业类型下拉菜单
     */
    private String type;
    /**
     * 任务信息
     * 例子：{"name": "test", "note": "作业描述", "statement": "sql 语句", "type": "kubernetes-session", "clusterId": 36}
     * 例子只列出了部分属性，其他属性请参考 TaskDTO 类
     */
    private TaskDTO task;
    /**
     * Dinky 推送时的作业配置
     * 例子： {"delayTime": 0, "taskPriority": "MEDIUM", "failRetryInterval": 2, "failRetryTimes": 3, "flag": "YES" }
     * 例子只列出了部分属性，其他属性请参考 DinkyTaskRequest 类
     */
    private DinkyTaskRequest jobConfig;
}

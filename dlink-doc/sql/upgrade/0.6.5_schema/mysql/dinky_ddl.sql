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

-- 0.6.5 2022-06-28
-- ----------------------------
alter table dlink_task
ADD COLUMN `version_id` INT NULL COMMENT '版本号ID';

CREATE TABLE IF NOT EXISTS `dlink_task_version`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `task_id` int(11) NOT NULL COMMENT '作业ID ',
    `version_id` int(11) NOT NULL COMMENT '版本ID ',
    `statement` text COMMENT 'flink sql 内容',
    `name` varchar(255) NOT NULL COMMENT '名称',
    `alias` varchar(255) DEFAULT NULL COMMENT '别名',
    `dialect` varchar(50) DEFAULT NULL COMMENT '方言',
    `type` varchar(50) DEFAULT NULL COMMENT '类型',
    `task_configure` text NOT NULL COMMENT '作业配置',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='作业历史版本';
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


-- 0.6.6 2022-07-23
-- ----------------------------
create table if not exists `metadata_database`
(
    `id` int(11) not null AUTO_INCREMENT COMMENT '主键',
    `database_name` varchar(255) NOT NULL COMMENT '名称',
    `description` varchar(255) null comment '描述',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='元数据对象信息';

create table if not exists `metadata_table`
(
    `id` int(11) not null AUTO_INCREMENT COMMENT '主键',
    `table_name` varchar(255) NOT NULL COMMENT '名称',
    `table_type` varchar(255) NOT null comment '对象类型，分为：database 和 table view',
    `database_id` int(11) not null COMMENT '数据库主键',
    `description` varchar(255) null comment '描述',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='元数据对象信息';

create table if not exists `metadata_database_property`
(
    `key` varchar(255) NOT NULL COMMENT '属性key',
    `value` varchar(255) NULL COMMENT '属性value',
    `database_id` int(11) not null COMMENT '数据库主键',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`key`, `database_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='元数据属性信息';

create table if not exists `metadata_table_property`
(
    `key` varchar(255) NOT NULL COMMENT '属性key',
    `value` varchar(255) NULL COMMENT '属性value',
    `table_id` int(11) not null COMMENT '数据表名称',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`key`, `table_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='元数据属性信息';

create table if not exists `metadata_column`
(
    `column_name` varchar(255) NOT NULL COMMENT '列名',
    `column_type` varchar(255) NOT NULL COMMENT '列类型, 有Physical Metadata Computed WATERMARK ',
    `data_type` varchar(255) NOT NULL COMMENT '数据类型',
    `expr` varchar(255) NULL COMMENT '表达式',
    `description` varchar(255) NOT NULL COMMENT '字段描述',
    `table_id` int(11) not null COMMENT '数据表名称',
    `primary` bit null comment '主键',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`table_id`, `column_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='数据列信息';

create table if not exists `metadata_function`
(
    `id` int(11) not null AUTO_INCREMENT COMMENT '主键',
    `function_name` varchar(255) NOT NULL COMMENT '名称',
    `class_name` varchar(255) NOT null comment '类名',
    `database_id` int(11) not null COMMENT '数据库主键',
    `function_language` varchar(255) null comment 'UDF语言',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='UDF信息';


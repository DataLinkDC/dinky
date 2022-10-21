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


-- ----------------------------------------------------------------------------- multi-tenant of start -------------------------------------------------------------------------------------------

-- 0.6.8 2022-10-13
-- ----------------------------
CREATE TABLE IF NOT EXISTS dlink_tenant
(
    id int auto_increment comment 'ID',
    tenant_code varchar(64) not null comment '租户编码',
    is_delete tinyint(1) default 0 not null comment '是否被删除',
    note varchar(255) null comment '注释',
    create_time datetime null comment '创建时间',
    update_time datetime null comment '最近修改时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT '租户';

CREATE TABLE IF NOT EXISTS dlink_role
(
    id int auto_increment comment 'ID',
    tenant_id int not null comment '租户ID',
    role_code varchar(64) not null comment '角色编码',
    role_name varchar(64) not null comment '角色名称',
    is_delete tinyint(1) default 0 not null comment '是否被删除',
    note varchar(255) null comment '注释',
    create_time datetime null comment '创建时间',
    update_time datetime null comment '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY `dlink_role_un` (`role_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC comment '角色';


CREATE TABLE IF NOT EXISTS dlink_namespace
(
    id int auto_increment comment 'ID',
    tenant_id int not null comment '租户ID',
    namespace_code varchar(64) not null comment '命名空间编码',
    enabled tinyint(1) default 1 not null comment '是否启用',
    note varchar(255) null comment '注释',
    create_time datetime null comment '创建时间',
    update_time datetime null comment '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY `dlink_namespace_un` (`namespace_code`, `tenant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC comment '命名空间';


CREATE TABLE IF NOT EXISTS dlink_role_namespace
(
    id int auto_increment comment 'ID',
    role_id int not null comment '用户ID',
    namespace_id int not null comment '名称空间ID',
    create_time datetime null comment '创建时间',
    update_time datetime null comment '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY `dlink_role_namespace_un` (role_id, namespace_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC comment '角色与名称空间关系';



CREATE TABLE IF NOT EXISTS dlink_user_role
(
    id int auto_increment comment 'ID',
    user_id int not null comment '用户ID',
    role_id int not null comment '角色ID',
    create_time datetime null comment '创建时间',
    update_time datetime null comment '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY `dlink_user_role_un` (user_id, role_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC comment '用户与角色关系';


CREATE TABLE IF NOT EXISTS `dlink_user_tenant`
(
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` int NOT NULL COMMENT '用户ID',
    `tenant_id` int NOT NULL COMMENT '租户ID',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `dlink_user_role_un` (`user_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='用户与租户关系';


alter table dlink_catalogue
add column tenant_id int not null comment '租户ID' after id;
alter table dlink_catalogue
drop index `idx_name`;
alter table dlink_catalogue
add unique key `dlink_catalogue_un` (`name`, `parent_id`, `tenant_id`);

alter table dlink_cluster
add column tenant_id int not null comment '租户ID' after id;
alter table dlink_cluster
drop index `idx_name`;
alter table dlink_cluster
add unique key `dlink_cluster_un` (`name`, `tenant_id`);

alter table dlink_task
add column tenant_id int not null comment '租户ID' after name;
alter table dlink_task
drop index `idx_name`;
alter table dlink_task
add unique key `dlink_task_un` (`name`, `tenant_id`);

alter table dlink_task_statement
add column tenant_id int not null comment '租户ID' after id;
alter table dlink_task_statement
add unique key `dlink_task_statement_un` (`tenant_id`, `id`);

alter table dlink_database
add column tenant_id int not null comment '租户ID' after id;
alter table dlink_database
drop index `db_index`;
alter table dlink_database
add unique key `dlink_database_un` (`name`, `tenant_id`);

alter table dlink_cluster_configuration
add column tenant_id int not null comment '租户ID' after id;
alter table dlink_cluster_configuration
add unique key `dlink_cluster_configuration_un` (`name`, `tenant_id`);

alter table dlink_jar
add column tenant_id int not null comment '租户ID' after id;
alter table dlink_jar
add unique key `dlink_jar_un` (`tenant_id`, `name`);

alter table dlink_savepoints
add column tenant_id int not null comment '租户ID' after task_id;


alter table dlink_job_instance
add column tenant_id int not null comment '租户ID' after name;
alter table dlink_job_instance
add unique key `dlink_job_instance_un` (`tenant_id`, `name`, `task_id`, `history_id`);

alter table dlink_alert_instance
add column tenant_id int not null comment '租户ID' after name;
alter table dlink_alert_instance
add unique key `dlink_alert_instance_un` (`name`, `tenant_id`);

alter table dlink_alert_group
add column tenant_id int not null comment '租户ID' after name;
alter table dlink_alert_group
add unique key `dlink_alert_instance_un` (`name`, `tenant_id`);

alter table dlink_alert_history
add column tenant_id int not null comment '租户ID' after id;

alter table dlink_task_version
add column tenant_id int not null comment '租户ID' after task_id;
alter table dlink_task_version
add unique key `dlink_task_version_un` (`task_id`, `tenant_id`, `version_id`);

alter table dlink_history
add column tenant_id int not null comment '租户ID' after id;
alter table dlink_job_history
add column tenant_id int not null comment '租户ID' after id;

-- 修改历史表的租户编号为默认租户
UPDATE `dlink_alert_group`
SET `tenant_id` = 1;
UPDATE `dlink_alert_history`
SET `tenant_id` = 1;
UPDATE `dlink_alert_instance`
SET `tenant_id` = 1;
UPDATE `dlink_catalogue`
SET `tenant_id` = 1;
UPDATE `dlink_cluster`
SET `tenant_id` = 1;
UPDATE `dlink_cluster_configuration`
SET `tenant_id` = 1;
UPDATE `dlink_database`
SET `tenant_id` = 1;
UPDATE `dlink_history`
SET `tenant_id` = 1;
UPDATE `dlink_jar`
SET `tenant_id` = 1;
UPDATE `dlink_job_instance`
SET `tenant_id` = 1;
UPDATE `dlink_savepoints`
SET `tenant_id` = 1;
UPDATE `dlink_task`
SET `tenant_id` = 1;
UPDATE `dlink_task_statement`
SET `tenant_id` = 1;
UPDATE `dlink_task_version`
SET `tenant_id` = 1;
UPDATE `dlink_job_history`
SET `tenant_id` = 1;


-- 0.6.8 2022-10-19
-- -----------------------
CREATE TABLE IF NOT EXISTS `dlink_udf`
(
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(50) DEFAULT NULL COMMENT 'udf名',
    `class_name` varchar(50) DEFAULT NULL COMMENT '完整的类名',
    `source_code` text COMMENT '源码',
    `compiler_code` binary(255) DEFAULT NULL COMMENT '编译产物',
    `version_id` int(11) DEFAULT NULL COMMENT '版本',
    `version_description` varchar(50) DEFAULT NULL COMMENT '版本描述',
    `is_default` tinyint(1) DEFAULT NULL COMMENT '是否默认',
    `document_id` int(11) DEFAULT NULL COMMENT '对应文档id',
    `from_version_id` int(11) DEFAULT NULL COMMENT '基于udf版本id',
    `code_md5` varchar(50) DEFAULT NULL COMMENT '源码',
    `dialect` varchar(50) DEFAULT NULL COMMENT '方言',
    `type` varchar(50) DEFAULT NULL COMMENT '类型',
    `step` int(255) DEFAULT NULL COMMENT '作业生命周期',
    `enable` tinyint(1) DEFAULT NULL COMMENT '是否启用',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ----------------------------
-- Table structure for dlink_udf_template
-- ----------------------------
CREATE TABLE IF NOT EXISTS `dlink_udf_template`
(
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(20) DEFAULT NULL COMMENT '模板名称',
    `code_type` varchar(10) DEFAULT NULL COMMENT '代码类型',
    `function_type` varchar(10) DEFAULT NULL COMMENT '函数类型',
    `template_code` text COMMENT '模板代码',
    `enabled` tinyint(1) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4;

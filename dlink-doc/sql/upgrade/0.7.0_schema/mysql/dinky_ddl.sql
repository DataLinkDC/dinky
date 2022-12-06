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


-- 0.6.8 2022-10-13
-- ----------------------------
-- ----------------------------
-- Table structure for dlink_tenant
-- ----------------------------
CREATE TABLE  IF NOT EXISTS `dlink_tenant` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'tenant code',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'is delete',
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
  `create_time` datetime DEFAULT NULL COMMENT 'create time',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='tenant';

-- ----------------------------
-- Table structure for dlink_role
-- ----------------------------
CREATE TABLE IF NOT EXISTS `dlink_role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tenant_id` int NOT NULL COMMENT 'tenant id',
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'role code',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'role name',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'is delete',
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
  `create_time` datetime DEFAULT NULL COMMENT 'create time',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `dlink_role_un` (`role_code`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='role';

-- ----------------------------
-- Table structure for dlink_namespace
-- ----------------------------
CREATE TABLE IF NOT EXISTS `dlink_namespace` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tenant_id` int NOT NULL COMMENT 'tenant id',
  `namespace_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'namespace code',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'is enable',
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
  `create_time` datetime DEFAULT NULL COMMENT 'create time',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `dlink_namespace_un` (`namespace_code`,`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='namespace';

-- ----------------------------
-- Table structure for dlink_role_namespace
-- ----------------------------
CREATE TABLE IF NOT EXISTS `dlink_role_namespace` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` int NOT NULL COMMENT 'user id',
  `namespace_id` int NOT NULL COMMENT 'namespace id',
  `create_time` datetime DEFAULT NULL COMMENT 'create time',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `dlink_role_namespace_un` (`role_id`,`namespace_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Role and namespace relationship';


-- ----------------------------
-- Table structure for dlink_user_role
-- ----------------------------
CREATE TABLE IF NOT EXISTS `dlink_user_role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int NOT NULL COMMENT 'user id',
  `role_id` int NOT NULL COMMENT 'role id',
  `create_time` datetime DEFAULT NULL COMMENT 'create time',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `dlink_user_role_un` (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Relationship between users and roles';


-- ----------------------------
-- Table structure for dlink_user_tenant
-- ----------------------------
CREATE TABLE IF NOT EXISTS `dlink_user_tenant` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int NOT NULL COMMENT 'user id',
  `tenant_id` int NOT NULL COMMENT 'tenant id',
  `create_time` datetime DEFAULT NULL COMMENT 'create time',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `dlink_user_role_un` (`user_id`,`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Relationship between users and tenants';


alter table dlink_catalogue add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after id;
alter table dlink_catalogue drop index `idx_name`;
alter table dlink_catalogue add unique key `dlink_catalogue_un` (`name`, `parent_id`, `tenant_id`);

alter table dlink_cluster add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after id;
alter table dlink_cluster drop index `idx_name`;
alter table dlink_cluster add unique key `dlink_cluster_un` (`name`, `tenant_id`);

alter table dlink_task add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after name;
alter table dlink_task drop index `idx_name`;
alter table dlink_task add unique key `dlink_task_un` (`name`, `tenant_id`);

alter table dlink_task_statement add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after id;
alter table dlink_task_statement add unique key `dlink_task_statement_un` (`tenant_id`, `id`);

alter table dlink_database add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after id;
alter table dlink_database drop index `db_index`;
alter table dlink_database add unique key `dlink_database_un` (`name`, `tenant_id`);

alter table dlink_cluster_configuration add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after id;
alter table dlink_cluster_configuration add unique key `dlink_cluster_configuration_un` (`name`, `tenant_id`);

alter table dlink_jar add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after id;
alter table dlink_jar add unique key `dlink_jar_un` (`tenant_id`, `name`);

alter table dlink_savepoints add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after task_id;


alter table dlink_job_instance add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after name;
alter table dlink_job_instance add unique key `dlink_job_instance_un` (`tenant_id`, `name`, `task_id`, `history_id`);

alter table dlink_alert_instance add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after name;
alter table dlink_alert_instance add unique key `dlink_alert_instance_un` (`name`, `tenant_id`);

alter table dlink_alert_group add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after name;
alter table dlink_alert_group add unique key `dlink_alert_instance_un` (`name`, `tenant_id`);

alter table dlink_alert_history add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after id;

alter table dlink_task_version add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after task_id;
alter table dlink_task_version add unique key `dlink_task_version_un` (`task_id`, `tenant_id`, `version_id`);

alter table dlink_history add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after id;
alter table dlink_job_history add column `tenant_id` int not null DEFAULT '1' comment 'tenant id' after id;

-- 0.6.8 2022-10-19
-- -----------------------

-- ----------------------------
-- Table structure for dlink_udf
-- ----------------------------
CREATE TABLE IF NOT EXISTS `dlink_udf` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'udf name',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Complete class name',
  `source_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'source code',
  `compiler_code` binary(255) DEFAULT NULL COMMENT 'compiler product',
  `version_id` int DEFAULT NULL COMMENT 'version',
  `version_description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'version description',
  `is_default` tinyint(1) DEFAULT NULL COMMENT 'Is it default',
  `document_id` int DEFAULT NULL COMMENT 'corresponding to the document id',
  `from_version_id` int DEFAULT NULL COMMENT 'Based on udf version id',
  `code_md5` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'source code of md5',
  `dialect` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'dialect',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'type',
  `step` int DEFAULT NULL COMMENT 'job lifecycle step',
  `enable` tinyint(1) DEFAULT NULL COMMENT 'is enable',
  `create_time` datetime DEFAULT NULL COMMENT 'create time',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT 'udf';
-- ----------------------------
-- Table structure for dlink_udf_template
-- ----------------------------
CREATE TABLE IF NOT EXISTS `dlink_udf_template` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '模板名称',
  `code_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '代码类型',
  `function_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '函数类型',
  `template_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '模板代码',
  `enabled` tinyint(1) DEFAULT NULL COMMENT 'is enable',
  `create_time` datetime DEFAULT NULL COMMENT 'create time',
  `update_time` datetime DEFAULT NULL COMMENT 'update time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT 'udf template';


alter table `dlink_task_statement` modify column `statement` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'statement set';
alter table `dlink_history` modify column `statement` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'statement set';
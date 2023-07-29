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

 SET NAMES utf8mb4;
 SET FOREIGN_KEY_CHECKS = 0;

-- rename table
ALTER TABLE dlink_alert_group RENAME dinky_alert_group;
ALTER TABLE dlink_alert_history RENAME dinky_alert_history;
ALTER TABLE dlink_alert_instance RENAME dinky_alert_instance;
ALTER TABLE dlink_catalogue RENAME dinky_catalogue;
ALTER TABLE dlink_cluster RENAME dinky_cluster;
ALTER TABLE dlink_cluster_configuration RENAME dinky_cluster_configuration;
ALTER TABLE dlink_database RENAME dinky_database;
ALTER TABLE dlink_flink_document RENAME dinky_flink_document;
ALTER TABLE dlink_fragment RENAME dinky_fragment;
ALTER TABLE dlink_history RENAME dinky_history;
ALTER TABLE dlink_jar RENAME dinky_jar;
ALTER TABLE dlink_job_history RENAME dinky_job_history;
ALTER TABLE dlink_job_instance RENAME dinky_job_instance;
ALTER TABLE dlink_namespace RENAME dinky_namespace;
ALTER TABLE dlink_role RENAME dinky_role;
ALTER TABLE dlink_role_namespace RENAME dinky_role_namespace;
ALTER TABLE dlink_savepoints RENAME dinky_savepoints;
ALTER TABLE dlink_schema_history RENAME dinky_schema_history;
ALTER TABLE dlink_sys_config RENAME dinky_sys_config;
ALTER TABLE dlink_task RENAME dinky_task;
ALTER TABLE dlink_task_statement RENAME dinky_task_statement;
ALTER TABLE dlink_task_version RENAME dinky_task_version;
ALTER TABLE dlink_tenant RENAME dinky_tenant;
ALTER TABLE dlink_udf RENAME dinky_udf;
ALTER TABLE dlink_udf_template RENAME dinky_udf_template;
ALTER TABLE dlink_upload_file_record RENAME dinky_upload_file_record;
ALTER TABLE dlink_user RENAME dinky_user;
ALTER TABLE dlink_user_role RENAME dinky_user_role;
ALTER TABLE dlink_user_tenant RENAME dinky_user_tenant;


-- rename index
ALTER TABLE dinky_alert_group RENAME INDEX `dlink_alert_instance_un` TO `alert_group_un_idx1`;
ALTER TABLE dinky_alert_instance RENAME INDEX `dlink_alert_instance_un` TO `alert_instance_un_idx1`;
ALTER TABLE dinky_cluster RENAME INDEX `dlink_cluster_un` TO `cluster_un_idx1`;
ALTER TABLE dinky_cluster_configuration RENAME INDEX `dlink_cluster_configuration_un` TO `cluster_configuration_un_idx1`;
ALTER TABLE dinky_database RENAME INDEX `dlink_database_un` TO `database_un_idx1`;
ALTER TABLE dinky_jar RENAME INDEX `dlink_jar_un` TO `jar_un_idx1`;
ALTER TABLE dinky_job_instance RENAME INDEX `dlink_job_instance_un` TO `job_instance_un_idx1`;
ALTER TABLE dinky_job_instance RENAME INDEX `dlink_job_instance_task_id_IDX` TO `job_instance_task_id_idx1`;
ALTER TABLE dinky_task RENAME INDEX `dlink_task_un` TO `task_un_idx1`;
ALTER TABLE dinky_task_statement RENAME INDEX `dlink_task_statement_un` TO `task_statement_un_idx1`;
ALTER TABLE dinky_task_version RENAME INDEX `dlink_task_version_un` TO `task_version_un_idx1`;
ALTER TABLE dinky_fragment RENAME INDEX `un_idx1` TO `fragment_un_idx1`;
ALTER TABLE dinky_namespace RENAME INDEX `dlink_namespace_un` TO `namespace_un_idx1`;
ALTER TABLE dinky_role RENAME INDEX `dlink_role_un` TO `role_un_idx1`;
ALTER TABLE dinky_role_namespace RENAME INDEX `dlink_role_namespace_un` TO `role_namespace_un_idx1`;
ALTER TABLE dinky_user_role RENAME INDEX `dlink_user_role_un` TO `user_role_un_idx1`;
ALTER TABLE dinky_user_tenant RENAME INDEX `dlink_user_role_un` TO `user_tenant_un_idx1`;
ALTER TABLE dinky_catalogue RENAME INDEX `dlink_catalogue_un` TO `catalogue_un_idx1`;
ALTER TABLE dinky_schema_history RENAME INDEX `dlink_schema_history_s_idx` TO `schema_history_idx`;

ALTER TABLE dinky_fragment DROP COLUMN `alias`;
ALTER TABLE dinky_database DROP COLUMN `alias`;
ALTER TABLE dinky_cluster_configuration DROP COLUMN `alias`;


ALTER TABLE dinky_task_version DROP COLUMN `alias`;
ALTER TABLE dinky_task DROP COLUMN `alias`;
ALTER TABLE dinky_jar DROP COLUMN `alias`;
CREATE TABLE `dinky_git_project` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                     `tenant_id` bigint(20) NOT NULL,
                                     `name` varchar(255) NOT NULL,
                                     `url` varchar(1000) NOT NULL,
                                     `branch` varchar(1000) NOT NULL,
                                     `username` varchar(255) DEFAULT NULL,
                                     `password` varchar(255) DEFAULT NULL,
                                     `private_key` varchar(255) DEFAULT NULL COMMENT 'keypath',
                                     `pom` varchar(255) DEFAULT NULL,
                                     `build_args` varchar(255) DEFAULT NULL,
                                     `code_type` tinyint(4) DEFAULT NULL COMMENT 'code type(1-java,2-python)',
                                     `type` tinyint(4) NOT NULL COMMENT '1-http ,2-ssh',
                                     `last_build` datetime DEFAULT NULL,
                                     `description` varchar(255) DEFAULT NULL,
                                     `build_state` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0-notStart 1-process 2-failed 3-success',
                                     `build_step` tinyint(2) NOT NULL DEFAULT '0',
                                     `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0-disable 1-enable',
                                     `udf_class_map_list` text COMMENT 'scan udf class',
                                     `order_line` int(11) NOT NULL DEFAULT '1' COMMENT 'order',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     KEY `tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;


ALTER TABLE dinky_role_select_permissions RENAME TO dinky_row_permissions;

ALTER TABLE dinky_user
    add  COLUMN `user_type` int default 0 not null comment 'login type（0:LOCAL,1:LDAP）' after user_type;


-- ----------------------------
-- Table structure for dinky_metrics
-- ----------------------------
CREATE TABLE `dinky_metrics` (
                                 `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                 `task_id` int(255) DEFAULT NULL COMMENT 'task id',
                                 `vertices` varchar(255) DEFAULT NULL COMMENT 'vertices',
                                 `metrics` varchar(255) DEFAULT NULL COMMENT 'metrics',
                                 `position` int(11) DEFAULT NULL COMMENT 'position',
                                 `show_type` varchar(255) DEFAULT NULL COMMENT 'show type',
                                 `show_size` varchar(255) DEFAULT NULL COMMENT 'show size',
                                 `title` varchar(255) DEFAULT NULL COMMENT 'title',
                                 `layout_name` varchar(255) DEFAULT NULL COMMENT 'layout name',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='metrics layout';


-- ----------------------------
-- Table structure for dinky_resources
-- ----------------------------
CREATE TABLE `dinky_resources` (
                                   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'key',
                                   `file_name` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT 'file name',
                                   `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                                   `user_id` int(11) DEFAULT NULL COMMENT 'user id',
                                   `type` tinyint(4) DEFAULT NULL COMMENT 'resource type,0:FILE，1:UDF',
                                   `size` bigint(20) DEFAULT NULL COMMENT 'resource size',
                                   `pid` int(11) DEFAULT NULL,
                                   `full_name` varchar(128) COLLATE utf8_bin DEFAULT NULL,
                                   `is_directory` tinyint(4) DEFAULT NULL,
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `dinky_resources_un` (`full_name`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- ----------------------------
-- Table structure for dinky_sys_login_log
-- ----------------------------
CREATE TABLE `dinky_sys_login_log` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'key',
  `user_id` int NOT NULL COMMENT 'user id',
  `username` varchar(60) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'username',
  `login_type` int NOT NULL COMMENT 'login type（0:LOCAL,1:LDAP）',
  `ip` varchar(40) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ip addr',
  `status` int NOT NULL COMMENT 'login status',
  `msg` text COLLATE utf8mb4_general_ci NOT NULL COMMENT 'status msg',
  `create_time` datetime NOT NULL COMMENT 'create time',
  `access_time` datetime DEFAULT NULL COMMENT 'access time',
  `update_time` datetime NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='system login log record'



-- ----------------------------
-- Table structure for dinky_sys_operate_log
-- ----------------------------
CREATE TABLE `dinky_sys_operate_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `module_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'module name',
  `business_type` int NULL DEFAULT 0 COMMENT 'business type',
  `method` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'method name',
  `request_method` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'request method',
  `operate_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'operate name',
  `operate_user_id` int NOT NULL COMMENT 'operate user id',
  `operate_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'operate url',
  `operate_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'ip',
  `operate_location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'operate location',
  `operate_param` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'request param',
  `json_result` longtext CHARACTER SET utf8 COLLATE utf8_general_ci  DEFAULT null COMMENT 'return json result',
  `status` int DEFAULT NULL COMMENT 'operate status',
  `error_msg` text CHARACTER SET utf8 COLLATE utf8_general_ci  DEFAULT NULL COMMENT 'error msg',
  `operate_time` datetime(0) NULL DEFAULT NULL COMMENT 'operate time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'operate log record' ROW_FORMAT = Dynamic;


alter table dinky_user
    add super_admin_flag tinyint default 0 null comment 'is super admin(0:false,1true)' after enabled;

alter table dinky_user_tenant
    add tenant_admin_flag tinyint default 0 null comment 'tenant admin flag(0:false,1:true)' after tenant_id;




SET FOREIGN_KEY_CHECKS = 1;

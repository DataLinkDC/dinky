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

begin ;
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
                                     `build_step` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'different from java and python, when build java project, the step value is as follows: 0: environment check 1: clone project 2: compile and build 3: get artifact 4: analyze UDF 5: finish; when build python project, the step value is as follows: 0: environment check 1: clone project 2: get artifact 3: analyze UDF 4: finish',
                                     `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0-disable 1-enable',
                                     `udf_class_map_list` text COMMENT 'scan udf class',
                                     `order_line` int(11) NOT NULL DEFAULT '1' COMMENT 'order',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     KEY `tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='git project';


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC collate = utf8mb4_general_ci COMMENT='metrics layout';


-- ----------------------------
-- Table structure for dinky_resources
-- ----------------------------
CREATE TABLE `dinky_resources` (
                                   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'key',
                                   `file_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'file name',
                                   `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                                   `user_id` int(11) DEFAULT NULL COMMENT 'user id',
                                   `type` tinyint(4) DEFAULT NULL COMMENT 'resource type,0:FILE，1:UDF',
                                   `size` bigint(20) DEFAULT NULL COMMENT 'resource size',
                                   `pid` int(11) DEFAULT NULL,
                                   `full_name` text COLLATE utf8mb4_general_ci DEFAULT NULL,
                                   `is_directory` tinyint(4) DEFAULT NULL,
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `dinky_resources_un` (`full_name`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='resource table';


ALTER TABLE dinky_database modify password varchar(512) null comment 'password';


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='system login log record';


-- ----------------------------
-- Table structure for dinky_sys_operate_log
-- ----------------------------
CREATE TABLE `dinky_sys_operate_log` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                         `module_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'module name',
                                         `business_type` int DEFAULT '0' COMMENT 'business type',
                                         `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'method name',
                                         `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'request method',
                                         `operate_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'operate name',
                                         `operate_user_id` int NOT NULL COMMENT 'operate user id',
                                         `operate_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'operate url',
                                         `operate_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'ip',
                                         `operate_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'operate location',
                                         `operate_param` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'request param',
                                         `json_result` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'return json result',
                                         `status` int DEFAULT NULL COMMENT 'operate status',
                                         `error_msg` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'error msg',
                                         `operate_time` datetime DEFAULT NULL COMMENT 'operate time',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='operate log record';

alter table dinky_user
    add super_admin_flag tinyint default 0 comment 'is super admin(0:false,1true)' after enabled;

alter table dinky_user_tenant add tenant_admin_flag tinyint default 0  comment 'tenant admin flag(0:false,1:true)' after tenant_id;

alter table dinky_task add column `statement` longtext DEFAULT NULL COMMENT 'job statement';

drop table dinky_namespace;
drop table dinky_role_namespace;



-- ----------------------------
-- Table structure for dinky_sys_menu
-- ----------------------------
create table `dinky_sys_menu` (
                                  `id` bigint not null auto_increment comment ' id',
                                  `parent_id` bigint not null comment 'parent menu id',
                                  `name` varchar(64) collate utf8mb4_general_ci not null comment 'menu button name',
                                  `path` varchar(64) collate utf8mb4_general_ci default null comment 'routing path',
                                  `component` varchar(64) collate utf8mb4_general_ci default null comment 'routing component component',
                                  `perms` varchar(64) collate utf8mb4_general_ci default null comment 'authority id',
                                  `icon` varchar(64) collate utf8mb4_general_ci default null comment 'icon',
                                  `type` char(1) collate utf8mb4_general_ci default null comment 'type(M:directory C:menu F:button)',
                                  `display` tinyint collate utf8mb4_general_ci not null default 1 comment 'whether the menu is displayed',
                                  `order_num` int default null comment 'sort',
                                  `create_time` datetime not null default current_timestamp comment 'create time',
                                  `update_time` datetime not null default current_timestamp on update current_timestamp comment 'modify time',
                                  `note` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                                  primary key (`id`) using btree
) engine=innodb auto_increment=1 default charset=utf8mb4 collate=utf8mb4_general_ci;


-- ----------------------------
-- Table structure dinky_sys_role_menu
-- ----------------------------
CREATE TABLE `dinky_sys_role_menu` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                       `role_id` bigint NOT NULL COMMENT 'role id',
                                       `menu_id` bigint NOT NULL COMMENT 'menu id',
                                       `create_time` datetime not null default current_timestamp comment 'create time',
                                       `update_time` datetime not null default current_timestamp on update current_timestamp comment 'modify time',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE KEY `un_role_menu_inx` (`role_id`,`menu_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE if not exists `dinky_alert_template` (
                                        `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                        `name` varchar(20) CHARACTER SET ucs2 COLLATE ucs2_general_ci DEFAULT NULL COMMENT 'template name',
                                        `template_content` text COLLATE utf8mb4_general_ci COMMENT 'template content',
                                        `enabled` tinyint DEFAULT '1' COMMENT 'is enable',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                        `creator` int(11) DEFAULT NULL COMMENT 'create user id',
                                        `updater` int(11) DEFAULT NULL COMMENT 'update user id',
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='alert template';

CREATE TABLE if not exists `dinky_alert_rules` (
                                     `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                     `name` varchar(40) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'rule name',
                                     `rule` text COLLATE utf8mb4_general_ci COMMENT 'specify rule',
                                     `template_id` int DEFAULT NULL COMMENT 'template id',
                                     `rule_type` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'alert rule type',
                                     `trigger_conditions` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'trigger conditions',
                                     `description` text COLLATE utf8mb4_general_ci COMMENT 'description',
                                     `enabled` tinyint DEFAULT 1 COMMENT 'is enable',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                     `creator` int(11) DEFAULT NULL COMMENT 'create user id',
                                     `updater` int(11) DEFAULT NULL COMMENT 'update user id',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='alert rule';

-- ----------------------------
-- Table structure dinky_sys_token
-- ----------------------------
CREATE TABLE if not exists `dinky_sys_token` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                   `token_value` varchar(255) NOT NULL COMMENT 'token value',
                                   `user_id` bigint(20) NOT NULL COMMENT 'user id',
                                   `role_id` bigint(20) NOT NULL COMMENT 'role id',
                                   `tenant_id` bigint(20) NOT NULL COMMENT 'tenant id',
                                   `expire_type` tinyint(4) NOT NULL COMMENT '1: never expire, 2: expire after a period of time, 3: expire at a certain time',
                                   `expire_start_time` datetime DEFAULT NULL COMMENT 'expire start time ,when expire_type = 3 , it is the start time of the period',
                                   `expire_end_time` datetime DEFAULT NULL COMMENT 'expire end time ,when expire_type = 2,3 , it is the end time of the period',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                   `source` tinyint(2) DEFAULT NULL COMMENT '1:login 2:custom',
                                   `creator` int(20) DEFAULT NULL COMMENT '创建人',
                                   `updater` int(20) DEFAULT NULL COMMENT '修改人',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `token_value` (`token_value`) USING BTREE,
                                   KEY `source` (`source`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC collate = utf8mb4_general_ci COMMENT='token management';

CREATE TABLE `dinky_udf_manage` (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `name` varchar(50) DEFAULT NULL COMMENT 'udf name',
                                    `class_name` varchar(50) DEFAULT NULL COMMENT 'Complete class name',
                                    `task_id` int(11) DEFAULT NULL COMMENT 'task id',
                                    `resources_id` int(11) DEFAULT NULL COMMENT 'resources id',
                                    `enabled` tinyint(1) DEFAULT 1 COMMENT 'is enable',
                                    `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                    `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    KEY `name,resources_id` (`name`,`resources_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='udf';


drop table if exists dinky_task_statement;
drop table if exists dinky_jar;
drop table if exists dinky_schema_history;
drop table if exists dinky_upload_file_record;
drop table if exists dinky_udf;

commit ;





-- 增加 创建人/修改人/操作人字段 相关 | added creator/updater/operator field

begin ;
-- 增加 dinky_alert_group 表的 创建人和修改人字段 字段类型 int 为创建人/修改人 id | added creator/updater field
alter table dinky_alert_group add column `creator` int(11) default null comment 'create user id';
alter table dinky_alert_group add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_alert_instance 表的 创建人和修改人字段 字段类型 int 为创建人/修改人 id | added creator/updater field
alter table dinky_alert_instance add column `creator` int(11) default null comment 'create user id';
alter table dinky_alert_instance add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_alert_template 表的 创建人和修改人字段 字段类型 int 为创建人/修改人 id | added creator/updater field
alter table dinky_alert_template add column `creator` int(11) default null comment 'create user id';
alter table dinky_alert_template add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_alert_rules 表的 创建人和修改人字段 字段类型 int 为创建人/修改人 id | added creator/updater field
alter table dinky_alert_rules add column `creator` int(11) default null comment 'create user id';
alter table dinky_alert_rules add column `updater` int(11) default null comment 'update user id';


-- 增加 dinky_catalogue 表的 creator 和 updater 字段 | added creator/updater field
alter table dinky_catalogue add column `creator` int(11) default null comment 'create user id';
alter table dinky_catalogue add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_task 表的 creator 和 updater 字段 operator 字段 | added creator/updater/operator field
alter table dinky_task add column `creator` int(11) default null comment 'create user id';
alter table dinky_task add column `updater` int(11) default null comment 'update user id';
alter table dinky_task add column `operator` int(11) default null comment 'operator user id';

-- 增加 dinky_task_version 表的 creator  | added creator field
alter table dinky_task_version add column `creator` int(11) default null comment 'create user id';

-- 增减 dinky_cluster_configuration 表的 creator 和 updater 字段 | added creator/updater field
alter table dinky_cluster_configuration add column `creator` int(11) default null comment 'create user id';
alter table dinky_cluster_configuration add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_cluster 表的 creator 和 updater 字段 | added creator/updater field
alter table dinky_cluster add column `creator` int(11) default null comment 'create user id';
alter table dinky_cluster add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_database 表的 creator 和 updater 字段 | added creator/updater field
alter table dinky_database add column `creator` int(11) default null comment 'create user id';
alter table dinky_database add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_flink_document 表的 creator 和 updater 字段 | added creator/updater field
alter table dinky_flink_document add column `creator` int(11) default null comment 'create user id';
alter table dinky_flink_document add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_fragment 表的 creator 和 updater 字段 | added creator/updater field
alter table dinky_fragment add column `creator` int(11) default null comment 'create user id';
alter table dinky_fragment add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_git_project 表的 creator 和 updater 字段 operator 字段 | added creator/updater/operator field
alter table dinky_git_project add column `creator` int(11) default null comment 'create user id';
alter table dinky_git_project add column `updater` int(11) default null comment 'update user id';
alter table dinky_git_project add column `operator` int(11) default null comment 'operator user id';

-- 增加 dinky_udf_manager 表的 creator 和 updater 字段 operator 字段 | added creator/updater/operator field
alter table dinky_udf_manage add column `creator` int(11) default null comment 'create user id';
alter table dinky_udf_manage add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_udf_template 表的 creator 和 updater 字段 | added creator/updater field
alter table dinky_udf_template add column `creator` int(11) default null comment 'create user id';
alter table dinky_udf_template add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_savepoints 表的 creator 字段 | added creator field
alter table dinky_savepoints add column `creator` int(11) default null comment 'create user id';

-- 增加dinky_resources 表的 creator 字段 updater 字段 | added creator/updater field
alter table dinky_resources add column `creator` int(11) default null comment 'create user id';
alter table dinky_resources add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_row_permissions 表的 creator 字段 updater 字段 | added creator/updater field
alter table dinky_row_permissions add column `creator` int(11) default null comment 'create user id';
alter table dinky_row_permissions add column `updater` int(11) default null comment 'update user id';

-- 增加 dinky_job_instance 表的 creator 字段 updater 字段 operator 字段 | added creator/updater/operator field
alter table dinky_job_instance add column `creator` int(11) default null comment 'create user id';
alter table dinky_job_instance add column `updater` int(11) default null comment 'update user id';
alter table dinky_job_instance add column `operator` int(11) default null comment 'operator user id';

-- 增加 dinky_sys_token 表的 creator 字段 updater 字段 | added creator/updater field
alter table dinky_sys_token add column `creator` int(11) default null comment 'create user id';
alter table dinky_sys_token add column `updater` int(11) default null comment 'update user id';


commit ;



alter table dinky_alert_instance  modify params json null comment 'configuration';


alter table dinky_database
    add connect_config text null after type;

-- 顺序执行问题 只能放在这里 不能放在 dml 语句中
update dinky_database set connect_config = json_object(
        'ip', ip,
        'port', port,
        'url', url,
        'username', username,
        'password', password
);


alter table dinky_database
    drop column ip;

alter table dinky_database
    drop column port;

alter table dinky_database
    drop column url;

alter table dinky_database
    drop column username;

alter table dinky_database
    drop column password;

alter table dinky_history
    modify job_id varchar(255) null comment 'Job ID';



SET FOREIGN_KEY_CHECKS = 1;

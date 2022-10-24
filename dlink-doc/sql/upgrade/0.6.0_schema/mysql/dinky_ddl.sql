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

-- ----------------------------
-- Table structure for dlink_catalogue
-- ----------------------------
CREATE TABLE if not exists `dlink_catalogue`(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `task_id` int(11) NULL DEFAULT NULL COMMENT 'Job ID',
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Job Name',
    `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Job Type',
    `parent_id` int(11) NOT NULL DEFAULT 0 COMMENT 'parent ID',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'is enable',
    `is_leaf` tinyint(1) NOT NULL COMMENT 'is leaf node',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_name` (`name`, `parent_id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4  COLLATE = utf8mb4_general_ci COMMENT = 'catalogue'  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_cluster
-- ----------------------------
CREATE TABLE if not exists `dlink_cluster`(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cluster instance name',
    `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'cluster instance alias',
    `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'cluster types',
    `hosts` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'cluster hosts',
    `job_manager_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Job Manager Host',
    `status` int(1) NULL DEFAULT NULL COMMENT 'cluster status',
    `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'note',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'is enable',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_name` (`name`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4  COLLATE = utf8mb4_general_ci COMMENT = 'cluster instance management'  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_task
-- ----------------------------
CREATE TABLE if not exists `dlink_task`(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Job name',
    `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Job alias',
    `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Job type',
    `check_point` int(11) NULL DEFAULT NULL COMMENT 'CheckPoint trigger seconds',
    `save_point_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SavePointPath',
    `parallelism` int(4) NULL DEFAULT NULL COMMENT 'parallelism',
    `fragment` tinyint(1) NULL DEFAULT NULL COMMENT 'fragment',
    `cluster_id` int(11) NULL DEFAULT NULL COMMENT 'Flink cluster ID',
    `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Job Note',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'is enable',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_name` (`name`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4  COLLATE = utf8mb4_general_ci COMMENT = 'Task'  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_task_statement
-- ----------------------------
CREATE TABLE if not exists `dlink_task_statement`(
    `id` int(11) NOT NULL COMMENT 'ID',
    `statement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'statement set',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4  COLLATE = utf8mb4_general_ci COMMENT = 'statement'  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_flink_document
-- ----------------------------
CREATE TABLE if not exists `dlink_flink_document`(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'document category',
    `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'document type',
    `subtype` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'document subtype',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'document name',
    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'document description',
    `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'document version such as:(flink1.12,flink1.13,flink1.14,flink1.15)',
    `like_num` int(255) NULL DEFAULT 0 COMMENT 'like number',
    `enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'is enable',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT 'update_time',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB   CHARACTER SET = utf8mb4  COLLATE = utf8mb4_general_ci COMMENT = 'flink document management'  ROW_FORMAT = Dynamic;


ALTER TABLE `dlink_flink_document` modify column `description` longtext;
ALTER TABLE `dlink_flink_document` ADD COLUMN `fill_value` longtext NULL COMMENT 'fill value' AFTER `description`;


-- ----------------------------
-- Table structure for dlink_history
-- ----------------------------
CREATE TABLE if not exists `dlink_history`(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `cluster_id` int(11) NOT NULL DEFAULT 0 COMMENT 'cluster ID',
    `session` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'session',
    `job_id` varchar(50) NULL DEFAULT NULL COMMENT 'Job ID',
    `job_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Job Name',
    `job_manager_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JJobManager Address',
    `status` int(1) NOT NULL DEFAULT 0 COMMENT 'status',
    `type` varchar(50) NULL DEFAULT NULL COMMENT 'job type',
    `statement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'statement set',
    `error` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'error message',
    `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'result set',
    `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'configurations',
    `start_time` datetime(0) NULL DEFAULT NULL COMMENT 'job start time',
    `end_time` datetime(0) NULL DEFAULT NULL COMMENT 'job end time',
    `task_id` int(11) NULL DEFAULT NULL COMMENT 'task ID',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `task_index` (`task_id`) USING BTREE,
    INDEX `cluster_index` (`cluster_id`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4  COLLATE = utf8mb4_general_ci COMMENT = 'execution history'  ROW_FORMAT = Dynamic;

ALTER TABLE `dlink_task` ADD COLUMN `config` text NULL COMMENT 'configuration' AFTER `cluster_id`;

-- ----------------------------
-- Table structure for dlink_database
-- ----------------------------
CREATE TABLE if not exists `dlink_database` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'database name',
    `alias` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'database alias',
    `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'Default' COMMENT 'database belong group name',
    `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'database type',
    `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'database ip',
    `port` int(11) NULL DEFAULT NULL COMMENT 'database port',
    `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'database url',
    `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'username',
    `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'password',
    `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'note',
    `db_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'version，such as: 11g of oracle ，2.2.3 of hbase',
    `status` tinyint(1) NULL COMMENT 'heartbeat status',
    `health_time` datetime(0) NULL DEFAULT NULL COMMENT 'last heartbeat time of trigger',
    `heartbeat_time` datetime(0) NULL DEFAULT NULL COMMENT 'last heartbeat time',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'is enable',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `db_index` (`name`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8mb4  COLLATE = utf8mb4_general_ci COMMENT 'database management' ROW_FORMAT = Dynamic;

ALTER TABLE `dlink_cluster` ADD COLUMN `version` varchar(20) NULL COMMENT 'version' AFTER `job_manager_host`;



-- ----------------------------
-- Table structure for dlink_cluster_configuration
-- ----------------------------
CREATE TABLE if not exists `dlink_cluster_configuration`(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cluster configuration name',
    `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'cluster configuration alias',
    `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'cluster type',
    `config_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'json of configuration',
    `is_available` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'is available',
    `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
    `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'is enable',
    `create_time` datetime DEFAULT NULL COMMENT 'create time',
    `update_time` datetime DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4  COLLATE = utf8mb4_general_ci COMMENT 'cluster configuration management' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_jar
-- ----------------------------
CREATE TABLE if not exists `dlink_jar`(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'jar name',
    `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'jar alias',
    `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'jar type',
    `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'file path',
    `main_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'application of main class',
    `paras` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'main class of args',
    `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
    `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'is enable',
    `create_time` datetime DEFAULT NULL COMMENT 'create time',
    `update_time` datetime DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4  COLLATE = utf8mb4_general_ci COMMENT 'jar management' ROW_FORMAT = Dynamic;

ALTER TABLE `dlink_task` ADD COLUMN `cluster_configuration_id` int(11) NULL COMMENT 'cluster configuration ID' AFTER `cluster_id`;

ALTER TABLE `dlink_task` ADD COLUMN `statement_set` tinyint(1) NULL COMMENT 'enable statement set' AFTER `fragment`;

alter table dlink_history add cluster_configuration_id int(11) null COMMENT 'cluster configuration id' after cluster_id;

-- ----------------------------
-- Table structure for dlink_sys_config
-- ----------------------------
CREATE TABLE if not exists `dlink_sys_config`(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'configuration name',
    `value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'configuration value',
    `create_time` datetime DEFAULT NULL COMMENT 'create time',
    `update_time` datetime DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4  COLLATE = utf8mb4_general_ci COMMENT 'system configuration' ROW_FORMAT = Dynamic;

alter table dlink_cluster add auto_registers tinyint(1) default 0 null comment 'is auto registration' after note;


ALTER TABLE `dlink_cluster` ADD COLUMN `cluster_configuration_id` int(11) NULL COMMENT 'cluster configuration id' AFTER `auto_registers`;

ALTER TABLE `dlink_cluster` ADD COLUMN `task_id` int(11) NULL COMMENT 'task ID' AFTER `cluster_configuration_id`;

-- ----------------------------
-- Table structure for dlink_savepoints
-- ----------------------------
CREATE TABLE if not exists `dlink_savepoints`(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `task_id` int(11) NOT NULL COMMENT 'task ID',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'task name',
    `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'savepoint type',
    `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'savepoint path',
    `create_time` datetime DEFAULT NULL COMMENT 'create time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4  COLLATE = utf8mb4_general_ci COMMENT 'job savepoint management' ROW_FORMAT = Dynamic;

ALTER TABLE `dlink_task` ADD COLUMN `save_point_strategy` int(1) NULL COMMENT 'SavePoint strategy' AFTER `check_point`;

-- ----------------------------
-- 0.4.0 2021-11-24
-- ----------------------------
ALTER TABLE `dlink_task` ADD COLUMN `jar_id` int(11) NULL COMMENT 'Jar ID' AFTER `cluster_configuration_id`;

-- ----------------------------
-- 0.4.0 2021-11-28
-- ----------------------------
-- ----------------------------
-- Table structure for dlink_user
-- ----------------------------
CREATE TABLE if not exists `dlink_user`(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'username',
    `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'password',
    `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'nickname',
    `worknum` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'worknum',
    `avatar` blob NULL COMMENT 'avatar',
    `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'mobile phone',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'is enable',
    `is_delete` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'is delete',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT 'create time',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB  CHARACTER SET = utf8mb4  COLLATE = utf8mb4_general_ci COMMENT 'user' ROW_FORMAT = Dynamic;


-- ----------------------------
-- 0.4.0 2021-11-29
-- ----------------------------
ALTER TABLE `dlink_task` CHANGE COLUMN `config` `config_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'configuration json' AFTER `jar_id`;

-- ----------------------------
-- 0.5.0 2021-12-13
-- ----------------------------
ALTER TABLE `dlink_task` ADD COLUMN `dialect` varchar(50) NULL COMMENT 'dialect' AFTER `alias`;
ALTER TABLE `dlink_task` ADD COLUMN `database_id` int(11) NULL COMMENT 'database ID' AFTER `cluster_configuration_id`;

-- ----------------------------
-- 0.5.0 2021-12-29
-- ----------------------------
ALTER TABLE `dlink_task` ADD COLUMN `env_id` int(11) NULL COMMENT 'env id' AFTER `jar_id`;

-- ----------------------------
-- 0.6.0 2022-01-28
-- ----------------------------
ALTER TABLE `dlink_database` ADD COLUMN `flink_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Flink configuration' AFTER `note`;

-- ----------------------------
-- 0.6.0 2022-02-02
-- ----------------------------
-- ----------------------------
-- Table structure for dlink_job_instance
-- ----------------------------
CREATE TABLE if not exists `dlink_job_instance`(
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'job instance name',
    `task_id` int DEFAULT NULL COMMENT 'task ID',
    `cluster_id` int DEFAULT NULL COMMENT 'cluster ID',
    `jid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Flink JobId',
    `status` int DEFAULT NULL COMMENT 'instance status',
    `history_id` int DEFAULT NULL COMMENT 'execution history ID',
    `create_time` datetime DEFAULT NULL COMMENT 'create time',
    `update_time` datetime DEFAULT NULL COMMENT 'update time',
    `finish_time` datetime DEFAULT NULL COMMENT 'finish time',
    `error` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'error logs',
    `failed_restart_count` int DEFAULT NULL COMMENT 'failed restart count',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  DEFAULT CHARSET = utf8mb4  COLLATE = utf8mb4_general_ci  ROW_FORMAT = DYNAMIC COMMENT ='job instance';


ALTER TABLE `dlink_task` ADD COLUMN `step` int(11) NULL COMMENT 'Job lifecycle' AFTER `note`;


-- ----------------------------
-- 0.6.0 2022-02-07
-- ----------------------------
ALTER TABLE `dlink_task` ADD COLUMN `batch_model` tinyint(1) NULL DEFAULT 0 COMMENT 'use batch model' AFTER `statement_set`;
-- ----------------------------
-- 0.6.0 2022-02-18
-- ----------------------------
ALTER TABLE `dlink_database` ADD COLUMN `flink_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Flink template' AFTER `flink_config`;

-- ----------------------------
-- 0.6.0 2022-02-22
-- ----------------------------
ALTER TABLE `dlink_job_instance` MODIFY COLUMN status varchar(50) NULL COMMENT 'job instance status';

-- ----------------------------
-- 0.6.0 2022-02-24
-- ----------------------------
-- ----------------------------
-- Table structure for dlink_alert_instance
-- ----------------------------
CREATE TABLE if not exists `dlink_alert_instance`(
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'alert instance name',
    `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'alert instance type such as: DingTalk,Wechat(Webhook,app) Feishu ,email',
    `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'configuration',
    `enabled` tinyint DEFAULT 1 COMMENT 'is enable',
    `create_time` datetime DEFAULT NULL COMMENT 'create time',
    `update_time` datetime DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  DEFAULT CHARSET = utf8mb4  COLLATE = utf8mb4_general_ci  ROW_FORMAT = DYNAMIC COMMENT ='Alert instance';

-- ----------------------------
-- Table structure for dlink_alert_group
-- ----------------------------
CREATE TABLE if not exists `dlink_alert_group`(
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'alert group name',
    `alert_instance_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Alert instance IDS',
    `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'note',
    `enabled` tinyint DEFAULT 1 COMMENT 'is enable',
    `create_time` datetime DEFAULT NULL COMMENT 'create time',
    `update_time` datetime DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  AUTO_INCREMENT = 3  DEFAULT CHARSET = utf8mb4  COLLATE = utf8mb4_general_ci  ROW_FORMAT = DYNAMIC COMMENT ='Alert group';

-- ----------------------------
-- Table structure for dlink_alert_history
-- ----------------------------
CREATE TABLE if not exists `dlink_alert_history`(
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
    `alert_group_id` int DEFAULT NULL COMMENT 'Alert group ID',
    `job_instance_id` int DEFAULT NULL COMMENT 'job instance ID',
    `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'alert title',
    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'content description',
    `status` int DEFAULT NULL COMMENT 'alert status',
    `log` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'log',
    `create_time` datetime DEFAULT NULL COMMENT 'create time',
    `update_time` datetime DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  DEFAULT CHARSET = utf8mb4  COLLATE = utf8mb4_general_ci  ROW_FORMAT = DYNAMIC COMMENT ='Alert history';

-- ----------------------------
-- 0.6.0 2022-02-25
-- ----------------------------
ALTER TABLE `dlink_job_instance` MODIFY COLUMN name varchar(255) NULL COMMENT 'job instance name';
-- ----------------------------
-- 0.6.0 2022-02-28
-- ----------------------------
ALTER TABLE `dlink_job_instance` ADD COLUMN `duration` BIGINT NULL COMMENT 'job duration' AFTER `finish_time`;

-- ----------------------------
-- 0.6.0 2022-03-01
-- ----------------------------
-- ----------------------------
-- Table structure for dlink_job_history
-- ----------------------------
CREATE TABLE if not exists `dlink_job_history`(
    `id` int NOT NULL COMMENT 'id',
    `job_json` json DEFAULT NULL COMMENT 'Job information json',
    `exceptions_json` json DEFAULT NULL COMMENT 'error message json',
    `checkpoints_json` json DEFAULT NULL COMMENT 'checkpoints json',
    `checkpoints_config_json` json DEFAULT NULL COMMENT 'checkpoints configuration json',
    `config_json` json DEFAULT NULL COMMENT 'configuration',
    `jar_json` json DEFAULT NULL COMMENT 'Jar configuration',
    `cluster_json` json DEFAULT NULL COMMENT 'cluster instance configuration',
    `cluster_configuration_json` json DEFAULT NULL COMMENT 'cluster config',
    `update_time` datetime DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  DEFAULT CHARSET = utf8mb4  COLLATE = utf8mb4_general_ci  ROW_FORMAT = DYNAMIC COMMENT ='Job history details';

-- ----------------------------
-- 0.6.0 2021-03-02
-- ----------------------------
ALTER TABLE `dlink_history` CHANGE COLUMN `config` `config_json` json NULL COMMENT 'config json' AFTER `result`;
-- ----------------------------
-- 0.6.0-SNAPSHOT 2022-03-04
-- ----------------------------
ALTER TABLE `dlink_task` ADD COLUMN `job_instance_id` BIGINT NULL COMMENT 'job instance id' AFTER `step`;
ALTER TABLE `dlink_task` ADD COLUMN `alert_group_id` BIGINT NULL COMMENT 'alert group id' AFTER `env_id`;
-- ----------------------------
-- 0.6.0 2022-03-13
-- ----------------------------
ALTER TABLE `dlink_job_instance` ADD COLUMN `step` INT NULL COMMENT 'job lifecycle' AFTER `task_id`;
-- ----------------------------
-- 0.6.0 2022-03-15
-- ----------------------------
CREATE INDEX dlink_job_instance_task_id_IDX USING BTREE ON dlink_job_instance (task_id);
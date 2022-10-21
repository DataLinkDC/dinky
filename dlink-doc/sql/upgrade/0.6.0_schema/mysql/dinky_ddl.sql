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
CREATE TABLE if not exists `dlink_catalogue`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `task_id` int(11) NULL DEFAULT NULL COMMENT '任务ID',
    `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
    `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '类型',
    `parent_id` int(11) NOT NULL DEFAULT 0 COMMENT '父ID',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
    `is_leaf` tinyint(1) NOT NULL COMMENT '是否为叶子',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_name` (`name`, `parent_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '目录'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_cluster
-- ----------------------------
CREATE TABLE if not exists `dlink_cluster`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
    `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
    `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
    `hosts` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'HOSTS',
    `job_manager_host` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'JMhost',
    `status` int(1) NULL DEFAULT NULL COMMENT '状态',
    `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_name` (`name`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '集群'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_task
-- ----------------------------
CREATE TABLE if not exists `dlink_task`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
    `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
    `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
    `check_point` int(11) NULL DEFAULT NULL COMMENT 'CheckPoint ',
    `save_point_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'SavePointPath',
    `parallelism` int(4) NULL DEFAULT NULL COMMENT 'parallelism',
    `fragment` tinyint(1) NULL DEFAULT NULL COMMENT 'fragment',
    `cluster_id` int(11) NULL DEFAULT NULL COMMENT 'Flink集群ID',
    `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_name` (`name`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '作业'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_task_statement
-- ----------------------------
CREATE TABLE if not exists `dlink_task_statement`
(
    `id` int(11) NOT NULL COMMENT 'ID',
    `statement` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '语句',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '语句'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_flink_document
-- ----------------------------
CREATE TABLE if not exists `dlink_flink_document`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文档类型',
    `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
    `subtype` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '子类型',
    `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '信息',
    `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
    `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本号',
    `like_num` int(255) NULL DEFAULT 0 COMMENT '喜爱值',
    `enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 263
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '文档管理'
  ROW_FORMAT = Dynamic;

ALTER TABLE `dlink_flink_document`
auto_increment = 1;

ALTER TABLE `dlink_flink_document`
modify column description longtext;
ALTER TABLE `dlink_flink_document`
ADD COLUMN `fill_value` longtext NULL COMMENT '填充值' AFTER `description`;


-- ----------------------------
-- Table structure for dlink_history
-- ----------------------------
CREATE TABLE if not exists `dlink_history`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `cluster_id` int(11) NOT NULL DEFAULT 0 COMMENT '集群ID',
    `session` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会话',
    `job_id` varchar(50) NULL DEFAULT NULL COMMENT 'JobID',
    `job_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作业名',
    `job_manager_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'JM地址',
    `status` int(1) NOT NULL DEFAULT 0 COMMENT '状态',
    `type` varchar(50) NULL DEFAULT NULL COMMENT '类型',
    `statement` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '语句集',
    `error` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '异常信息',
    `result` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '结果集',
    `config` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '配置',
    `start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
    `task_id` int(11) NULL DEFAULT NULL COMMENT '作业ID',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `task_index` (`task_id`) USING BTREE,
    INDEX `cluster_index` (`cluster_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '执行历史'
  ROW_FORMAT = Dynamic;

ALTER TABLE `dlink_task`
ADD COLUMN `config` text NULL COMMENT '配置' AFTER `cluster_id`;

-- ----------------------------
-- Table structure for dlink_database
-- ----------------------------
CREATE TABLE if not exists `dlink_database`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源名',
    `alias` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源标题',
    `group_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'Default' COMMENT '数据源分组',
    `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型',
    `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'IP',
    `port` int(11) NULL DEFAULT NULL COMMENT '端口号',
    `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'url',
    `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
    `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
    `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
    `db_version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本，如oracle的11g，hbase的2.2.3',
    `status` tinyint(1) NULL COMMENT '状态',
    `health_time` datetime(0) NULL DEFAULT NULL COMMENT '最近健康时间',
    `heartbeat_time` datetime(0) NULL DEFAULT NULL COMMENT '最近心跳检测时间',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `db_index` (`name`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

ALTER TABLE `dlink_cluster`
ADD COLUMN `version` varchar(20) NULL COMMENT '版本' AFTER `job_manager_host`;

ALTER TABLE `dlink_flink_document`
ADD COLUMN `fill_value` varchar(255) NULL COMMENT '填充值' AFTER `description`;


CREATE TABLE if not exists `dlink_cluster_configuration`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
    `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '别名',
    `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '类型',
    `config_json` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '配置JSON',
    `is_available` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '是否可用',
    `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '注释',
    `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

CREATE TABLE if not exists `dlink_jar`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
    `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '别名',
    `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '类型',
    `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件路径',
    `main_class` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '启动类',
    `paras` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '启动类入参',
    `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '注释',
    `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

ALTER TABLE `dlink_task`
ADD COLUMN `cluster_configuration_id` int(11) NULL COMMENT '集群配置ID' AFTER `cluster_id`;

ALTER TABLE `dlink_task`
ADD COLUMN `statement_set` tinyint(1) NULL COMMENT '启用语句集' AFTER `fragment`;

alter table dlink_history
add cluster_configuration_id int(11) null COMMENT '集群配置ID' after cluster_id;

CREATE TABLE if not exists `dlink_sys_config`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '配置名',
    `value` text CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '值',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

alter table dlink_cluster
add auto_registers tinyint(1) default 0 null comment '是否自动注册' after note;

update dlink_cluster
set type ='yarn-session'
where type = 'Yarn';
update dlink_cluster
set type ='standalone'
where type = 'Standalone';

ALTER TABLE `dlink_cluster`
ADD COLUMN `cluster_configuration_id` int(11) NULL COMMENT '集群配置ID' AFTER `auto_registers`;

ALTER TABLE `dlink_cluster`
ADD COLUMN `task_id` int(11) NULL COMMENT '任务ID' AFTER `cluster_configuration_id`;

CREATE TABLE if not exists `dlink_savepoints`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `task_id` int(11) NOT NULL COMMENT '任务ID',
    `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
    `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型',
    `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '路径',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

ALTER TABLE `dlink_task`
ADD COLUMN `save_point_strategy` int(1) NULL COMMENT 'SavePoint策略' AFTER `check_point`;

-- ----------------------------
-- 0.4.0 2021-11-24
-- ----------------------------
ALTER TABLE `dlink_task`
ADD COLUMN `jar_id` int(11) NULL COMMENT 'JarID' AFTER `cluster_configuration_id`;

-- ----------------------------
-- 0.4.0 2021-11-28
-- ----------------------------
CREATE TABLE if not exists `dlink_user`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录名',
    `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
    `nickname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
    `worknum` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工号',
    `avatar` blob NULL COMMENT '头像',
    `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
    `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `is_delete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否被删除',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;


-- ----------------------------
-- 0.4.0 2021-11-29
-- ----------------------------
ALTER TABLE `dlink_task`
CHANGE COLUMN `config` `config_json` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '配置JSON' AFTER `jar_id`;

-- ----------------------------
-- 0.5.0 2021-12-13
-- ----------------------------
ALTER TABLE `dlink_task`
ADD COLUMN `dialect` varchar(50) NULL COMMENT '方言' AFTER `alias`;
ALTER TABLE `dlink_task`
ADD COLUMN `database_id` int(11) NULL COMMENT '数据源ID' AFTER `cluster_configuration_id`;

-- ----------------------------
-- 0.5.0 2021-12-29
-- ----------------------------
ALTER TABLE `dlink_task`
ADD COLUMN `env_id` int(11) NULL COMMENT '环境ID' AFTER `jar_id`;

-- ----------------------------
-- 0.6.0 2022-01-28
-- ----------------------------
ALTER TABLE `dlink_database`
ADD COLUMN `flink_config` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Flink配置' AFTER `note`;

-- ----------------------------
-- 0.6.0 2022-02-02
-- ----------------------------
-- ----------------------------
-- Table structure for dlink_job_instance
-- ----------------------------
CREATE TABLE if not exists `dlink_job_instance`
(
    `id` int NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '作业实例名',
    `task_id` int DEFAULT NULL COMMENT 'taskID',
    `cluster_id` int DEFAULT NULL COMMENT '集群ID',
    `jid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'FlinkJobId',
    `status` int DEFAULT NULL COMMENT '实例状态',
    `history_id` int DEFAULT NULL COMMENT '提交历史ID',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
    `error` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '异常日志',
    `failed_restart_count` int DEFAULT NULL COMMENT '重启次数',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='作业实例';


ALTER TABLE `dlink_task`
ADD COLUMN `step` int(11) NULL COMMENT '作业生命周期' AFTER `note`;

-- ----------------------------
-- 0.6.0 2022-02-03
-- ----------------------------
update dlink_task
set dialect = 'FlinkJar'
where jar_id is not null;
update dlink_catalogue
set type = 'FlinkJar'
where task_id in (select id as task_id from dlink_task where jar_id is not null);

-- ----------------------------
-- 0.6.0 2022-02-07
-- ----------------------------
ALTER TABLE `dlink_task`
ADD COLUMN `batch_model` tinyint(1) NULL DEFAULT 0 COMMENT '使用批模式' AFTER `statement_set`;
-- ----------------------------
-- 0.6.0 2022-02-18
-- ----------------------------
ALTER TABLE `dlink_database`
ADD COLUMN `flink_template` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Flink模板' AFTER `flink_config`;

-- ----------------------------
-- 0.6.0 2022-02-22
-- ----------------------------
ALTER TABLE `dlink_job_instance`
MODIFY COLUMN status varchar(50) NULL COMMENT '实例状态';

-- ----------------------------
-- 0.6.0 2022-02-24
-- ----------------------------
CREATE TABLE if not exists `dlink_alert_instance`
(
    `id` int NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '类型',
    `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '配置',
    `enabled` tinyint DEFAULT 1 COMMENT '是否启用',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='Alert实例';


CREATE TABLE if not exists `dlink_alert_group`
(
    `id` int NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `alert_instance_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Alert实例IDS',
    `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '说明',
    `enabled` tinyint DEFAULT 1 COMMENT '是否启用',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='Alert组';


CREATE TABLE if not exists `dlink_alert_history`
(
    `id` int NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `alert_group_id` int DEFAULT NULL COMMENT 'Alert组ID',
    `job_instance_id` int DEFAULT NULL COMMENT '作业实例ID',
    `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标题',
    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '正文',
    `status` int DEFAULT NULL COMMENT '状态',
    `log` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '日志',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='Alert历史';

-- ----------------------------
-- 0.6.0 2022-02-25
-- ----------------------------
ALTER TABLE `dlink_job_instance`
MODIFY COLUMN name varchar(255) NULL COMMENT '作业实例名';
-- ----------------------------
-- 0.6.0 2022-02-28
-- ----------------------------
ALTER TABLE `dlink_job_instance`
ADD COLUMN `duration` BIGINT NULL COMMENT '耗时' AFTER `finish_time`;

-- ----------------------------
-- 0.6.0 2022-03-01
-- ----------------------------
CREATE TABLE if not exists `dlink_job_history`
(
    `id` int NOT NULL COMMENT '实例主键',
    `job_json` json DEFAULT NULL COMMENT 'Job信息',
    `exceptions_json` json DEFAULT NULL COMMENT '异常日志',
    `checkpoints_json` json DEFAULT NULL COMMENT '保存点',
    `checkpoints_config_json` json DEFAULT NULL COMMENT '保存点配置',
    `config_json` json DEFAULT NULL COMMENT '配置',
    `jar_json` json DEFAULT NULL COMMENT 'Jar配置',
    `cluster_json` json DEFAULT NULL COMMENT '集群实例',
    `cluster_configuration_json` json DEFAULT NULL COMMENT '集群配置',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='Job历史详情';


-- ----------------------------
-- 0.6.0 2021-03-02
-- ----------------------------
ALTER TABLE `dlink_history`
CHANGE COLUMN `config` `config_json` json NULL COMMENT '配置JSON' AFTER `result`;
-- ----------------------------
-- 0.6.0-SNAPSHOT 2022-03-04
-- ----------------------------
ALTER TABLE `dlink_task`
ADD COLUMN `job_instance_id` BIGINT NULL COMMENT '任务实例ID' AFTER `step`;
ALTER TABLE `dlink_task`
ADD COLUMN `alert_group_id` BIGINT NULL COMMENT '报警组ID' AFTER `env_id`;
-- ----------------------------
-- 0.6.0 2022-03-13
-- ----------------------------
ALTER TABLE `dlink_job_instance`
ADD COLUMN `step` INT NULL COMMENT '生命周期' AFTER `task_id`;
-- ----------------------------
-- 0.6.0 2022-03-15
-- ----------------------------
CREATE INDEX dlink_job_instance_task_id_IDX USING BTREE ON dlink_job_instance (task_id);
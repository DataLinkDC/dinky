/*
 Navicat Premium Data Transfer

 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Schema         : dlink

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 24/11/2021 09:19:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dlink_catalogue
-- ----------------------------
DROP TABLE IF EXISTS `dlink_catalogue`;
CREATE TABLE `dlink_catalogue`  (
	`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`task_id` int(11) NULL DEFAULT NULL COMMENT '任务ID',
	`name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
	`type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
	`parent_id` int(11) NOT NULL DEFAULT 0 COMMENT '父ID',
	`enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
	`is_leaf` tinyint(1) NOT NULL COMMENT '是否为文件夹',
	`create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
	`update_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
	PRIMARY KEY (`id`) USING BTREE,
	UNIQUE INDEX `idx_name`(`name`, `parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '目录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_cluster
-- ----------------------------
DROP TABLE IF EXISTS `dlink_cluster`;
CREATE TABLE `dlink_cluster`  (
	`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
	`alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
	`type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
	`hosts` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'HOSTS',
	`job_manager_host` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'JMhost',
	`version` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本',
	`status` int(1) NULL DEFAULT NULL COMMENT '状态',
	`note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
	`auto_registers` tinyint(1) NULL DEFAULT 0 COMMENT '是否自动注册',
	`cluster_configuration_id` int(11) NULL DEFAULT NULL COMMENT '集群配置ID',
	`task_id` int(11) NULL DEFAULT NULL COMMENT '任务ID',
	`enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
	`create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
	`update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
	PRIMARY KEY (`id`) USING BTREE,
	UNIQUE INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '集群' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_cluster_configuration
-- ----------------------------
DROP TABLE IF EXISTS `dlink_cluster_configuration`;
CREATE TABLE `dlink_cluster_configuration`  (
	`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
	`alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
	`type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
	`config_json` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '配置JSON',
	`is_available` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否可用',
	`note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
	`enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
	`create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
	`update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
	PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_database
-- ----------------------------
DROP TABLE IF EXISTS `dlink_database`;
CREATE TABLE `dlink_database`  (
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
	`status` tinyint(1) NULL DEFAULT 0 COMMENT '状态',
	`health_time` datetime(0) NULL DEFAULT NULL COMMENT '最近健康时间',
	`heartbeat_time` datetime(0) NULL DEFAULT NULL COMMENT '最近心跳监测时间',
	`enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
	`create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
	`update_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
	PRIMARY KEY (`id`) USING BTREE,
	UNIQUE INDEX `db_index`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_flink_document
-- ----------------------------
DROP TABLE IF EXISTS `dlink_flink_document`;
CREATE TABLE `dlink_flink_document`  (
	`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`category` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文档类型',
	`type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
	`subtype` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '子类型',
	`name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '信息',
	`description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
	`fill_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '填充值',
	`version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本号',
	`like_num` int(255) NULL DEFAULT 0 COMMENT '喜爱值',
	`enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用',
	`create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
	`update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
	PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 264 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文档管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_history
-- ----------------------------
DROP TABLE IF EXISTS `dlink_history`;
CREATE TABLE `dlink_history`  (
	`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`cluster_id` int(11) NOT NULL DEFAULT 0 COMMENT '集群ID',
	`cluster_configuration_id` int(11) NULL DEFAULT NULL,
	`session` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会话',
	`job_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'JobID',
	`job_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作业名',
	`job_manager_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'JM地址',
	`status` int(1) NOT NULL DEFAULT 0 COMMENT '状态',
	`type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
	`statement` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '语句集',
	`error` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '异常信息',
	`result` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '结果集',
	`config` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '配置',
	`start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
	`end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
	`task_id` int(11) NULL DEFAULT NULL COMMENT '作业ID',
	PRIMARY KEY (`id`) USING BTREE,
	INDEX `task_index`(`task_id`) USING BTREE,
	INDEX `cluster_index`(`cluster_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 209 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '执行历史' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_jar
-- ----------------------------
DROP TABLE IF EXISTS `dlink_jar`;
CREATE TABLE `dlink_jar`  (
	`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
	`alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
	`type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
	`path` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '路径',
	`main_class` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '启动类',
	`paras` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '启动类入参',
	`note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
	`enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
	`create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
	`update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
	PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_savepoints
-- ----------------------------
DROP TABLE IF EXISTS `dlink_savepoints`;
CREATE TABLE `dlink_savepoints`  (
	`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`task_id` int(11) NOT NULL COMMENT '任务ID',
	`name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
	`type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
	`path` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '路径',
	`create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
	PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_schema_history
-- ----------------------------
DROP TABLE IF EXISTS `dlink_schema_history`;
CREATE TABLE `dlink_schema_history`  (
	`installed_rank` int(11) NOT NULL,
	`version` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
	`description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
	`type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
	`script` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
	`checksum` int(11) NULL DEFAULT NULL,
	`installed_by` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
	`installed_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`execution_time` int(11) NOT NULL,
	`success` tinyint(1) NOT NULL,
	PRIMARY KEY (`installed_rank`) USING BTREE,
	INDEX `dlink_schema_history_s_idx`(`success`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `dlink_sys_config`;
CREATE TABLE `dlink_sys_config`  (
	`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '配置名',
	`value` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '值',
	`create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
	`update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
	PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_task
-- ----------------------------
DROP TABLE IF EXISTS `dlink_task`;
CREATE TABLE `dlink_task`  (
	`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
	`alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
	`type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
	`check_point` int(11) NULL DEFAULT NULL COMMENT 'CheckPoint ',
	`save_point_strategy` int(1) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT 'SavePoint策略',
	`save_point_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'SavePointPath',
	`parallelism` int(4) NULL DEFAULT NULL COMMENT 'parallelism',
	`fragment` tinyint(1) NULL DEFAULT NULL COMMENT 'fragment',
	`statement_set` tinyint(1) NULL DEFAULT NULL COMMENT '启用语句集',
	`cluster_id` int(11) NULL DEFAULT NULL COMMENT 'Flink集群ID',
	`cluster_configuration_id` int(11) NULL DEFAULT NULL COMMENT '集群配置ID',
	`jar_id` int(11) NULL DEFAULT NULL COMMENT 'jarID',
	`config` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '配置',
	`note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
	`enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
	`create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
	`update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
	PRIMARY KEY (`id`) USING BTREE,
	UNIQUE INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '作业' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dlink_task_statement
-- ----------------------------
DROP TABLE IF EXISTS `dlink_task_statement`;
CREATE TABLE `dlink_task_statement`  (
	`id` int(11) NOT NULL COMMENT 'ID',
	`statement` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '语句',
	PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '语句' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

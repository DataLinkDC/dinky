/*
 Navicat Premium Data Transfer

 Source Server         : 10.1.51.25
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : 10.1.51.25:3306
 Source Schema         : dlink

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 28/05/2021 18:56:01
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
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '类型',
  `parent_id` int(11) NOT NULL DEFAULT 0 COMMENT '父ID',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
  `is_leaf` tinyint(1) NOT NULL COMMENT '是否为叶子',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_name`(`name`, `parent_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '目录' ROW_FORMAT = Dynamic;

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
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '集群' ROW_FORMAT = Dynamic;

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
  `save_point_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'SavePointPath',
  `parallelism` int(4) NULL DEFAULT NULL COMMENT 'parallelism',
  `fragment` tinyint(255) NULL DEFAULT NULL COMMENT 'fragment',
  `cluster_id` int(11) NULL DEFAULT NULL COMMENT 'Flink集群ID',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '作业' ROW_FORMAT = Dynamic;

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

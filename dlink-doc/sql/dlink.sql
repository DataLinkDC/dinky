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
  `fragment` tinyint(1) NULL DEFAULT NULL COMMENT 'fragment',
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
  `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本号',
  `like_num` int(255) NULL DEFAULT 0 COMMENT '喜爱值',
  `enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 263 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文档管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dlink_flink_document
-- ----------------------------
INSERT INTO `dlink_flink_document` VALUES (1, 'function', '内置函数', '比较函数', 'value1 = value2', '如果value1等于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '1.12', 9, 1, '2021-02-22 10:06:49', '2021-02-24 09:40:30');
INSERT INTO `dlink_flink_document` VALUES (3, 'function', '内置函数', '比较函数', 'value1 <> value2', '如果value1不等于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '1.12', 4, 1, '2021-02-22 10:05:38', '2021-03-11 09:58:48');
INSERT INTO `dlink_flink_document` VALUES (22, 'function', '内置函数', '比较函数', 'value1 > value2', '如果value1大于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '1.12', 2, 1, '2021-02-22 14:37:58', '2021-03-10 11:58:06');
INSERT INTO `dlink_flink_document` VALUES (23, 'function', '内置函数', '比较函数', 'value1 >= value2', '如果value1大于或等于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '1.12', 2, 1, '2021-02-22 14:38:52', '2021-03-10 11:57:57');
INSERT INTO `dlink_flink_document` VALUES (24, 'function', '内置函数', '比较函数', 'value1 < value2', '如果value1小于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '1.12', 0, 1, '2021-02-22 14:39:15', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (25, 'function', '内置函数', '比较函数', 'value1 <= value2', '如果value1小于或等于value2 返回true; 如果value1或value2为NULL，则返回UNKNOWN 。', '1.12', 0, 1, '2021-02-22 14:39:40', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (26, 'function', '内置函数', '比较函数', 'value IS NULL', '如果value为NULL，则返回TRUE 。', '1.12', 2, 1, '2021-02-22 14:40:39', '2021-03-10 11:57:51');
INSERT INTO `dlink_flink_document` VALUES (27, 'function', '内置函数', '比较函数', 'value IS NOT NULL', '如果value不为NULL，则返回TRUE 。', '1.12', 0, 1, '2021-02-22 14:41:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (28, 'function', '内置函数', '比较函数', 'value1 IS DISTINCT FROM value2', '如果两个值不相等则返回TRUE。NULL值在这里被视为相同的值。', '1.12', 0, 1, '2021-02-22 14:42:39', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (29, 'function', '内置函数', '比较函数', 'value1 IS NOT DISTINCT FROM value2', '如果两个值相等则返回TRUE。NULL值在这里被视为相同的值。', '1.12', 0, 1, '2021-02-22 14:43:23', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (30, 'function', '内置函数', '比较函数', 'value1 BETWEEN [ ASYMMETRIC | SYMMETRIC ] value2 AND value3', '如果value1大于或等于value2和小于或等于value3 返回true', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (31, 'function', '内置函数', '比较函数', 'value1 NOT BETWEEN [ ASYMMETRIC | SYMMETRIC ] value2 AND value3', '如果value1小于value2或大于value3 返回true', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (32, 'function', '内置函数', '比较函数', 'string1 LIKE string2 [ ESCAPE char ]', '如果STRING1匹配模式STRING2，则返回TRUE ；如果STRING1或STRING2为NULL，则返回UNKNOWN 。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (33, 'function', '内置函数', '比较函数', 'string1 NOT LIKE string2 [ ESCAPE char ]', '如果STRING1不匹配模式STRING2，则返回TRUE ；如果STRING1或STRING2为NULL，则返回UNKNOWN 。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (34, 'function', '内置函数', '比较函数', 'string1 SIMILAR TO string2 [ ESCAPE char ]', '如果STRING1与SQL正则表达式STRING2匹配，则返回TRUE ；如果STRING1或STRING2为NULL，则返回UNKNOWN 。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-03-10 11:57:28');
INSERT INTO `dlink_flink_document` VALUES (35, 'function', '内置函数', '比较函数', 'string1 NOT SIMILAR TO string2 [ ESCAPE char ]', '如果STRING1与SQL正则表达式STRING2不匹配，则返回TRUE ；如果STRING1或STRING2为NULL，则返回UNKNOWN 。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (36, 'function', '内置函数', '比较函数', 'value1 IN (value2 [, value3]* )', '如果value1存在于给定列表（value2，value3，...）中，则返回TRUE 。\r\n\r\n当（value2，value3，...）包含NULL，如果可以找到该元素，则返回TRUE，否则返回UNKNOWN。\r\n\r\n如果value1为NULL，则始终返回UNKNOWN 。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (37, 'function', '内置函数', '比较函数', 'value1 NOT IN (value2 [, value3]* )', '如果value1不存在于给定列表（value2，value3，...）中，则返回TRUE 。\r\n\r\n当（value2，value3，...）包含NULL，如果可以找到该元素，则返回TRUE，否则返回UNKNOWN。\r\n\r\n如果value1为NULL，则始终返回UNKNOWN 。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (38, 'function', '内置函数', '比较函数', 'EXISTS (sub-query)', '如果value存在于子查询中，则返回TRUE。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (39, 'function', '内置函数', '比较函数', 'value IN (sub-query)', '如果value存在于子查询中，则返回TRUE。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (40, 'function', '内置函数', '比较函数', 'value NOT IN (sub-query)', '如果value不存在于子查询中，则返回TRUE。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (41, 'function', '内置函数', '逻辑函数', 'boolean1 OR boolean2', '如果BOOLEAN1为TRUE或BOOLEAN2为TRUE，则返回TRUE。支持三值逻辑。\r\n\r\n例如，true || Null(Types.BOOLEAN)返回TRUE。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (42, 'function', '内置函数', '逻辑函数', 'boolean1 AND boolean2', '如果BOOLEAN1和BOOLEAN2均为TRUE，则返回TRUE。支持三值逻辑。\r\n\r\n例如，true && Null(Types.BOOLEAN)返回未知。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (43, 'function', '内置函数', '逻辑函数', 'NOT boolean', '如果BOOLEAN为FALSE，则返回TRUE ；如果BOOLEAN为TRUE，则返回FALSE 。\r\n\r\n如果BOOLEAN为UNKNOWN，则返回UNKNOWN。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (44, 'function', '内置函数', '逻辑函数', 'boolean IS FALSE', '	\r\n如果BOOLEAN为FALSE，则返回TRUE ；如果BOOLEAN为TRUE或UNKNOWN，则返回FALSE 。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (45, 'function', '内置函数', '逻辑函数', 'boolean IS NOT FALSE', '如果BOOLEAN为TRUE或UNKNOWN，则返回TRUE ；如果BOOLEAN为FALSE，则返回FALSE。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (46, 'function', '内置函数', '逻辑函数', 'boolean IS TRUE', '如果BOOLEAN为TRUE，则返回TRUE；如果BOOLEAN为FALSE或UNKNOWN，则返回FALSE 。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (47, 'function', '内置函数', '逻辑函数', 'boolean IS NOT TRUE', '如果BOOLEAN为FALSE或UNKNOWN，则返回TRUE ；如果BOOLEAN为TRUE，则返回FALSE 。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (48, 'function', '内置函数', '逻辑函数', 'boolean IS UNKNOWN', '如果BOOLEAN为UNKNOWN，则返回TRUE ；如果BOOLEAN为TRUE或FALSE，则返回FALSE 。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (49, 'function', '内置函数', '逻辑函数', 'boolean IS NOT UNKNOWN', '如果BOOLEAN为TRUE或FALSE，则返回TRUE ；如果BOOLEAN为UNKNOWN，则返回FALSE 。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (50, 'function', '内置函数', '算术函数', '+ numeric', '返回NUMERIC。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (51, 'function', '内置函数', '算术函数', '- numeric', '返回负数NUMERIC。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (52, 'function', '内置函数', '算术函数', 'numeric1 + numeric2', '返回NUMERIC1加NUMERIC2。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (53, 'function', '内置函数', '算术函数', 'numeric1 - numeric2', '返回NUMERIC1减去NUMERIC2。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (54, 'function', '内置函数', '算术函数', 'numeric1 * numeric2', '返回NUMERIC1乘以NUMERIC2。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (55, 'function', '内置函数', '算术函数', 'numeric1 / numeric2', '返回NUMERIC1除以NUMERIC2。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (56, 'function', '内置函数', '算术函数', 'numeric1 % numeric2', '返回NUMERIC1除以NUMERIC2的余数（模）。仅当numeric1为负数时，结果为负数。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (57, 'function', '内置函数', '算术函数', 'POWER(numeric1, numeric2)', '返回NUMERIC1的NUMERIC2 次幂。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (58, 'function', '内置函数', '算术函数', 'ABS(numeric)', '返回NUMERIC的绝对值。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (59, 'function', '内置函数', '算术函数', 'MOD(numeric1, numeric2)', '返回numeric1除以numeric2的余数(模)。只有当numeric1为负数时，结果才为负数', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (60, 'function', '内置函数', '算术函数', 'SQRT(numeric)', '返回NUMERIC的平方根。', '1.12', 0, 1, '2021-02-22 14:44:26', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (61, 'function', '内置函数', '算术函数', 'LN(numeric)', '返回NUMERIC的自然对数（以e为底）。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (62, 'function', '内置函数', '算术函数', 'LOG10(numeric)', '返回NUMERIC的以10为底的对数。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (63, 'function', '内置函数', '算术函数', 'LOG2(numeric)', '返回NUMERIC的以2为底的对数。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (64, 'function', '内置函数', '算术函数', 'LOG(numeric2)\r\nLOG(numeric1, numeric2)', '如果不带参数调用，则返回NUMERIC1的自然对数。当使用参数调用时，将NUMERIC1的对数返回到基数NUMERIC2。\r\n\r\n注意：当前，NUMERIC1必须大于0，而NUMERIC2必须大于1。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (65, 'function', '内置函数', '算术函数', 'EXP(numeric)', '返回e 的 NUMERIC 次幂。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (66, 'function', '内置函数', '算术函数', 'CEIL(numeric)\r\nCEILING(numeric)', '将NUMERIC向上舍入，并返回大于或等于NUMERIC的最小整数。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (67, 'function', '内置函数', '算术函数', 'FLOOR(numeric)', '	\r\n向下舍入NUMERIC，并返回小于或等于NUMERIC的最大整数。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (68, 'function', '内置函数', '算术函数', 'SIN(numeric)', '返回NUMERIC的正弦值。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (69, 'function', '内置函数', '算术函数', 'SINH(numeric)', '返回NUMERIC的双曲正弦值。\r\n\r\n返回类型为DOUBLE。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (70, 'function', '内置函数', '算术函数', 'COS(numeric)', '返回NUMERIC的余弦值。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (71, 'function', '内置函数', '算术函数', 'TAN(numeric)', '返回NUMERIC的正切。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (72, 'function', '内置函数', '算术函数', 'TANH(numeric)', '返回NUMERIC的双曲正切值。\r\n\r\n返回类型为DOUBLE。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (73, 'function', '内置函数', '算术函数', 'COT(numeric)', '返回NUMERIC的余切。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (74, 'function', '内置函数', '算术函数', 'ASIN(numeric)', '返回NUMERIC的反正弦值。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (75, 'function', '内置函数', '算术函数', 'ACOS(numeric)', '返回NUMERIC的反余弦值。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (76, 'function', '内置函数', '算术函数', 'ATAN(numeric)', '返回NUMERIC的反正切。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (77, 'function', '内置函数', '算术函数', 'ATAN2(numeric1, numeric2)', '返回坐标的反正切（NUMERIC1，NUMERIC2）。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (78, 'function', '内置函数', '算术函数', 'COSH(numeric)', '返回NUMERIC的双曲余弦值。\r\n\r\n返回值类型为DOUBLE。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (79, 'function', '内置函数', '算术函数', 'DEGREES(numeric)', '返回弧度NUMERIC的度数表示形式', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (80, 'function', '内置函数', '算术函数', 'RADIANS(numeric)', '返回度数NUMERIC的弧度表示。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (81, 'function', '内置函数', '算术函数', 'SIGN(numeric)', '返回NUMERIC的符号。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (82, 'function', '内置函数', '算术函数', 'ROUND(numeric, integer)', '返回一个数字，四舍五入为NUMERIC的INT小数位。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (83, 'function', '内置函数', '算术函数', 'PI', '返回一个比任何其他值都更接近圆周率的值。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (84, 'function', '内置函数', '算术函数', 'E()', '返回一个比任何其他值都更接近e的值。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (85, 'function', '内置函数', '算术函数', 'RAND()', '返回介于0.0（含）和1.0（不含）之间的伪随机双精度值。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (86, 'function', '内置函数', '算术函数', 'RAND(integer)', '	\r\n返回带有初始种子INTEGER的介于0.0（含）和1.0（不含）之间的伪随机双精度值。\r\n\r\n如果两个RAND函数具有相同的初始种子，它们将返回相同的数字序列。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (87, 'function', '内置函数', '算术函数', 'RAND_INTEGER(integer)', '返回介于0（含）和INTEGER（不含）之间的伪随机整数值。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (88, 'function', '内置函数', '算术函数', 'RAND_INTEGER(integer1, integer2)', '返回介于0（含）和INTEGER2（不含）之间的伪随机整数值，其初始种子为INTEGER1。\r\n\r\n如果两个randInteger函数具有相同的初始种子和边界，它们将返回相同的数字序列。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (89, 'function', '内置函数', '算术函数', 'UUID()', '根据RFC 4122 type 4（伪随机生成）UUID返回UUID（通用唯一标识符）字符串\r\n\r\n（例如，“ 3d3c68f7-f608-473f-b60c-b0c44ad4cc4e”）。使用加密强度高的伪随机数生成器生成UUID。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (90, 'function', '内置函数', '算术函数', 'BIN(integer)', '以二进制格式返回INTEGER的字符串表示形式。如果INTEGER为NULL，则返回NULL。\r\n\r\n例如，4.bin()返回“ 100”并12.bin()返回“ 1100”。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (91, 'function', '内置函数', '算术函数', 'HEX(numeric)\r\nHEX(string)', '以十六进制格式返回整数NUMERIC值或STRING的字符串表示形式。如果参数为NULL，则返回NULL。\r\n\r\n例如，数字20导致“ 14”，数字100导致“ 64”，字符串“ hello，world”导致“ 68656C6C6F2C776F726C64”。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (92, 'function', '内置函数', '算术函数', 'TRUNCATE(numeric1, integer2)', '返回一个小数点后被截断为integer2位的数字。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (93, 'function', '内置函数', '算术函数', 'PI()', '返回π (pi)的值。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (94, 'function', '内置函数', '字符串函数', 'string1 || string2', '返回string1和string2的连接。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (95, 'function', '内置函数', '字符串函数', 'CHAR_LENGTH(string)\r\nCHARACTER_LENGTH(string)', '返回STRING中的字符数。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (96, 'function', '内置函数', '字符串函数', 'UPPER(string)', '以大写形式返回STRING。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (97, 'function', '内置函数', '字符串函数', 'LOWER(string)', '以小写形式返回STRING。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (98, 'function', '内置函数', '字符串函数', 'POSITION(string1 IN string2)', '返回STRING1在STRING2中第一次出现的位置（从1开始）；\r\n\r\n如果在STRING2中找不到STRING1，则返回0 。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (99, 'function', '内置函数', '字符串函数', 'TRIM([ BOTH | LEADING | TRAILING ] string1 FROM string2)', '返回一个字符串，该字符串从STRING中删除前导和/或结尾字符。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (100, 'function', '内置函数', '字符串函数', 'LTRIM(string)', '返回一个字符串，该字符串从STRING除去左空格。\r\n\r\n例如，\" This is a test String.\".ltrim()返回“This is a test String.”。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (101, 'function', '内置函数', '字符串函数', 'RTRIM(string)', '返回一个字符串，该字符串从STRING中删除正确的空格。\r\n\r\n例如，\"This is a test String. \".rtrim()返回“This is a test String.”。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (102, 'function', '内置函数', '字符串函数', 'REPEAT(string, integer)', '返回一个字符串，该字符串重复基本STRING INT次。\r\n\r\n例如，\"This is a test String.\".repeat(2)返回“This is a test String.This is a test String.”。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (103, 'function', '内置函数', '字符串函数', 'REGEXP_REPLACE(string1, string2, string3)', '返回字符串STRING1所有匹配正则表达式的子串STRING2连续被替换STRING3。\r\n\r\n例如，\"foobar\".regexpReplace(\"oo|ar\", \"\")返回“ fb”。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (104, 'function', '内置函数', '字符串函数', 'OVERLAY(string1 PLACING string2 FROM integer1 [ FOR integer2 ])', '从位置INT1返回一个字符串，该字符串将STRING1的INT2（默认为STRING2的长度）字符替换为STRING2', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (105, 'function', '内置函数', '字符串函数', 'SUBSTRING(string FROM integer1 [ FOR integer2 ])', '返回字符串STRING的子字符串，从位置INT1开始，长度为INT2（默认为结尾）。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (106, 'function', '内置函数', '字符串函数', 'REPLACE(string1, string2, string3)', '返回一个新字符串替换其中出现的所有STRING2与STRING3（非重叠）从STRING1。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (107, 'function', '内置函数', '字符串函数', 'REGEXP_EXTRACT(string1, string2[, integer])', '从STRING1返回一个字符串，该字符串使用指定的正则表达式STRING2和正则表达式匹配组索引INTEGER1提取。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (108, 'function', '内置函数', '字符串函数', 'INITCAP(string)', '返回一种新形式的STRING，其中每个单词的第一个字符转换为大写，其余字符转换为小写。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (109, 'function', '内置函数', '字符串函数', 'CONCAT(string1, string2,...)', '返回连接STRING1，STRING2，...的字符串。如果任何参数为NULL，则返回NULL。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (110, 'function', '内置函数', '字符串函数', 'CONCAT_WS(string1, string2, string3,...)', '返回一个字符串，会连接STRING2，STRING3，......与分离STRING1。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (111, 'function', '内置函数', '字符串函数', 'LPAD(string1, integer, string2)', '返回一个新字符串，该字符串从STRING1的左侧填充STRING2，长度为INT个字符。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (112, 'function', '内置函数', '字符串函数', 'RPAD(string1, integer, string2)', '返回一个新字符串，该字符串从STRING1右侧填充STRING2，长度为INT个字符。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (113, 'function', '内置函数', '字符串函数', 'FROM_BASE64(string)', '返回来自STRING的base64解码结果；如果STRING为NULL，则返回null 。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (114, 'function', '内置函数', '字符串函数', 'TO_BASE64(string)', '从STRING返回base64编码的结果；如果STRING为NULL，则返回NULL。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (115, 'function', '内置函数', '字符串函数', 'ASCII(string)', '返回字符串的第一个字符的数值。如果字符串为NULL，则返回NULL。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (116, 'function', '内置函数', '字符串函数', 'CHR(integer)', '返回与integer在二进制上等价的ASCII字符。如果integer大于255，我们将首先得到integer的模数除以255，并返回模数的CHR。如果integer为NULL，则返回NULL。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (117, 'function', '内置函数', '字符串函数', 'DECODE(binary, string)', '使用提供的字符集(\'US-ASCII\'， \'ISO-8859-1\'， \'UTF-8\'， \'UTF-16BE\'， \'UTF-16LE\'， \'UTF-16\'之一)将第一个参数解码为字符串。如果任意一个参数为空，结果也将为空。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (118, 'function', '内置函数', '字符串函数', 'ENCODE(string1, string2)', '使用提供的string2字符集(\'US-ASCII\'， \'ISO-8859-1\'， \'UTF-8\'， \'UTF-16BE\'， \'UTF-16LE\'， \'UTF-16\'之一)将string1编码为二进制。如果任意一个参数为空，结果也将为空。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (119, 'function', '内置函数', '字符串函数', 'INSTR(string1, string2)', '返回string2在string1中第一次出现的位置。如果任何参数为空，则返回NULL。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (120, 'function', '内置函数', '字符串函数', 'LEFT(string, integer)', '返回字符串中最左边的整数字符。如果整数为负，则返回空字符串。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (121, 'function', '内置函数', '字符串函数', 'RIGHT(string, integer)', '返回字符串中最右边的整数字符。如果整数为负，则返回空字符串。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (122, 'function', '内置函数', '字符串函数', 'LOCATE(string1, string2[, integer])', '返回string1在string2中的位置整数之后第一次出现的位置。如果没有找到，返回0。如果任何参数为NULL，则返回NULL仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (123, 'function', '内置函数', '字符串函数', 'PARSE_URL(string1, string2[, string3])', '从URL返回指定的部分。string2的有效值包括\'HOST\'， \'PATH\'， \'QUERY\'， \'REF\'， \'PROTOCOL\'， \'AUTHORITY\'， \'FILE\'和\'USERINFO\'。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (124, 'function', '内置函数', '字符串函数', 'REGEXP(string1, string2)', '如果string1的任何子字符串(可能为空)与Java正则表达式string2匹配，则返回TRUE，否则返回FALSE。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (125, 'function', '内置函数', '字符串函数', 'REVERSE(string)', '返回反向字符串。如果字符串为NULL，则返回NULL仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (126, 'function', '内置函数', '字符串函数', 'SPLIT_INDEX(string1, string2, integer1)', '通过分隔符string2拆分string1，返回拆分字符串的整数(从零开始)字符串。如果整数为负，返回NULL。如果任何参数为NULL，则返回NULL。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (127, 'function', '内置函数', '字符串函数', 'STR_TO_MAP(string1[, string2, string3]])', '使用分隔符将string1分割成键/值对后返回一个映射。string2是pair分隔符，默认为\'，\'。string3是键值分隔符，默认为\'=\'。仅在blink planner中支持。', '1.12', 4, 1, '2021-02-22 15:29:35', '2021-05-20 19:59:50');
INSERT INTO `dlink_flink_document` VALUES (128, 'function', '内置函数', '字符串函数', 'SUBSTR(string[, integer1[, integer2]])', '返回一个字符串的子字符串，从位置integer1开始，长度为integer2(默认到末尾)。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (129, 'function', '内置函数', '时间函数', 'DATE string', '返回以“ yyyy-MM-dd”形式从STRING解析的SQL日期。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (130, 'function', '内置函数', '时间函数', 'TIME string', '返回以“ HH：mm：ss”的形式从STRING解析的SQL时间。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (131, 'function', '内置函数', '时间函数', 'TIMESTAMP string', '返回从STRING解析的SQL时间戳，格式为“ yyyy-MM-dd HH：mm：ss [.SSS]”', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (132, 'function', '内置函数', '时间函数', 'INTERVAL string range', '解析“dd hh:mm:ss”形式的区间字符串。fff表示毫秒间隔，yyyy-mm表示月间隔。间隔范围可以是天、分钟、天到小时或天到秒，以毫秒为间隔;年或年到月的间隔。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (133, 'function', '内置函数', '时间函数', 'CURRENT_DATE', '返回UTC时区中的当前SQL日期。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (134, 'function', '内置函数', '时间函数', 'CURRENT_TIME', '返回UTC时区的当前SQL时间。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (135, 'function', '内置函数', '时间函数', 'CURRENT_TIMESTAMP', '返回UTC时区内的当前SQL时间戳。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (136, 'function', '内置函数', '时间函数', 'LOCALTIME', '返回本地时区的当前SQL时间。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (137, 'function', '内置函数', '时间函数', 'LOCALTIMESTAMP', '返回本地时区的当前SQL时间戳。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (138, 'function', '内置函数', '时间函数', 'EXTRACT(timeintervalunit FROM temporal)', '返回从时域的timeintervalunit部分提取的长值。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (139, 'function', '内置函数', '时间函数', 'YEAR(date)', '返回SQL date日期的年份。等价于EXTRACT(YEAR FROM date)。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (140, 'function', '内置函数', '时间函数', 'QUARTER(date)', '从SQL date date返回一年中的季度(1到4之间的整数)。相当于EXTRACT(从日期起四分之一)。', '1.12', 0, 1, '2021-02-22 15:29:35', '2021-02-22 15:28:47');
INSERT INTO `dlink_flink_document` VALUES (141, 'function', '内置函数', '时间函数', 'MONTH(date)', '返回SQL date date中的某月(1到12之间的整数)。等价于EXTRACT(MONTH FROM date)。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (142, 'function', '内置函数', '时间函数', 'WEEK(date)', '从SQL date date返回一年中的某个星期(1到53之间的整数)。相当于EXTRACT(从日期开始的星期)。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (143, 'function', '内置函数', '时间函数', 'DAYOFYEAR(date)', '返回SQL date date中的某一天(1到366之间的整数)。相当于EXTRACT(DOY FROM date)。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (144, 'function', '内置函数', '时间函数', 'DAYOFMONTH(date)', '从SQL date date返回一个月的哪一天(1到31之间的整数)。相当于EXTRACT(DAY FROM date)。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (145, 'function', '内置函数', '时间函数', 'DAYOFWEEK(date)', '返回星期几(1到7之间的整数;星期日= 1)从SQL日期日期。相当于提取(道指从日期)。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (146, 'function', '内置函数', '时间函数', 'HOUR(timestamp)', '从SQL timestamp timestamp返回一天中的小时(0到23之间的整数)。相当于EXTRACT(HOUR FROM timestamp)。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (147, 'function', '内置函数', '时间函数', 'MINUTE(timestamp)', '从SQL timestamp timestamp返回一小时的分钟(0到59之间的整数)。相当于EXTRACT(分钟从时间戳)。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (148, 'function', '内置函数', '时间函数', 'SECOND(timestamp)', '从SQL时间戳返回一分钟中的秒(0到59之间的整数)。等价于EXTRACT(从时间戳开始倒数第二)。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (149, 'function', '内置函数', '时间函数', 'FLOOR(timepoint TO timeintervalunit)', '返回一个将timepoint舍入到时间单位timeintervalunit的值。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (150, 'function', '内置函数', '时间函数', 'CEIL(timepoint TO timeintervalunit)', '返回一个将timepoint舍入到时间单位timeintervalunit的值。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (151, 'function', '内置函数', '时间函数', '(timepoint1, temporal1) OVERLAPS (timepoint2, temporal2)', '如果(timepoint1, temporal1)和(timepoint2, temporal2)定义的两个时间间隔重叠，则返回TRUE。时间值可以是时间点或时间间隔。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (152, 'function', '内置函数', '时间函数', 'DATE_FORMAT(timestamp, string)', '注意这个功能有严重的错误，现在不应该使用。请实现一个自定义的UDF，或者使用EXTRACT作为解决方案。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (153, 'function', '内置函数', '时间函数', 'TIMESTAMPADD(timeintervalunit, interval, timepoint)', '返回一个新的时间值，该值将一个(带符号的)整数间隔添加到时间点。间隔的单位由unit参数给出，它应该是以下值之一:秒、分、小时、日、周、月、季度或年。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (154, 'function', '内置函数', '时间函数', 'TIMESTAMPDIFF(timepointunit, timepoint1, timepoint2)', '返回timepointunit在timepoint1和timepoint2之间的(带符号)数。间隔的单位由第一个参数给出，它应该是以下值之一:秒、分、小时、日、月或年。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (155, 'function', '内置函数', '时间函数', 'CONVERT_TZ(string1, string2, string3)', '将时区string2中的datetime string1(默认ISO时间戳格式\'yyyy-MM-dd HH:mm:ss\')转换为时区string3。时区的格式可以是缩写，如“PST”;可以是全名，如“America/Los_Angeles”;或者是自定义ID，如“GMT-8:00”。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (156, 'function', '内置函数', '时间函数', 'FROM_UNIXTIME(numeric[, string])', '以字符串格式返回数值参数的表示形式(默认为\'yyyy-MM-dd HH:mm:ss\')。numeric是一个内部时间戳值，表示从UTC \'1970-01-01 00:00:00\'开始的秒数，例如UNIX_TIMESTAMP()函数生成的时间戳。返回值用会话时区表示(在TableConfig中指定)。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (157, 'function', '内置函数', '时间函数', 'UNIX_TIMESTAMP()', '获取当前Unix时间戳(以秒为单位)。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (158, 'function', '内置函数', '时间函数', 'UNIX_TIMESTAMP(string1[, string2])', '转换日期时间字符串string1，格式为string2(缺省为yyyy-MM-dd HH:mm:ss，如果没有指定)为Unix时间戳(以秒为单位)，使用表配置中指定的时区。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (159, 'function', '内置函数', '时间函数', 'TO_DATE(string1[, string2])', '将格式为string2的日期字符串string1(默认为\'yyyy-MM-dd\')转换为日期。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (160, 'function', '内置函数', '时间函数', 'TO_TIMESTAMP(string1[, string2])', '将会话时区(由TableConfig指定)下的日期时间字符串string1转换为时间戳，格式为string2(默认为\'yyyy-MM-dd HH:mm:ss\')。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (161, 'function', '内置函数', '时间函数', 'NOW()', '返回UTC时区内的当前SQL时间戳。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (162, 'function', '内置函数', '条件函数', 'CASE value\r\nWHEN value1_1 [, value1_2 ]* THEN result1\r\n[ WHEN value2_1 [, value2_2 ]* THEN result2 ]*\r\n[ ELSE resultZ ]\r\nEND', '当第一个时间值包含在(valueX_1, valueX_2，…)中时，返回resultX。如果没有匹配的值，则返回resultZ，否则返回NULL。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (163, 'function', '内置函数', '条件函数', 'CASE\r\nWHEN condition1 THEN result1\r\n[ WHEN condition2 THEN result2 ]*\r\n[ ELSE resultZ ]\r\nEND', '当第一个条件满足时返回resultX。当不满足任何条件时，如果提供了resultZ则返回resultZ，否则返回NULL。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (164, 'function', '内置函数', '条件函数', 'NULLIF(value1, value2)', '如果value1等于value2，则返回NULL;否则返回value1。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (165, 'function', '内置函数', '条件函数', 'COALESCE(value1, value2 [, value3 ]* )', '返回value1, value2， ....中的第一个非空值', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (166, 'function', '内置函数', '条件函数', 'IF(condition, true_value, false_value)', '如果条件满足则返回true值，否则返回false值。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (167, 'function', '内置函数', '条件函数', 'IS_ALPHA(string)', '如果字符串中所有字符都是字母则返回true，否则返回false。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (168, 'function', '内置函数', '条件函数', 'IS_DECIMAL(string)', '如果字符串可以被解析为有效的数字则返回true，否则返回false。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (169, 'function', '内置函数', '条件函数', 'IS_DIGIT(string)', '如果字符串中所有字符都是数字则返回true，否则返回false。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (170, 'function', '内置函数', '类型转换函数功能', 'CAST(value AS type)', '返回一个要转换为type类型的新值。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (171, 'function', '内置函数', 'Collection 函数', 'CARDINALITY(array)', '返回数组中元素的数量。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (172, 'function', '内置函数', 'Collection 函数', 'array ‘[’ integer ‘]’', '返回数组中位于整数位置的元素。索引从1开始。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (173, 'function', '内置函数', 'Collection 函数', 'ELEMENT(array)', '返回数组的唯一元素(其基数应为1);如果数组为空，则返回NULL。如果数组有多个元素，则抛出异常。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (174, 'function', '内置函数', 'Collection 函数', 'CARDINALITY(map)', '返回map中的条目数。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (175, 'function', '内置函数', 'Collection 函数', 'map ‘[’ value ‘]’', '返回map中key value指定的值。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (176, 'function', '内置函数', 'Value Construction函数', '-- implicit constructor with parenthesis\r\n(value1 [, value2]*)\r\n\r\n-- explicit ROW constructor\r\nROW(value1 [, value2]*)', '返回从值列表(value1, value2，…)创建的行。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (177, 'function', '内置函数', 'Value Construction函数', 'ARRAY ‘[’ value1 [, value2 ]* ‘]’', '返回一个由一系列值(value1, value2，…)创建的数组。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (178, 'function', '内置函数', 'Value Construction函数', 'MAP ‘[’ value1, value2 [, value3, value4 ]* ‘]’', '返回一个从键值对列表((value1, value2)， (value3, value4)，…)创建的映射。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (179, 'function', '内置函数', 'Value Access函数', 'tableName.compositeType.field', '按名称从Flink复合类型(例如，Tuple, POJO)中返回一个字段的值。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (180, 'function', '内置函数', 'Value Access函数', 'tableName.compositeType.*', '返回Flink复合类型(例如，Tuple, POJO)的平面表示，它将每个直接子类型转换为一个单独的字段。在大多数情况下，平面表示的字段的名称与原始字段类似，但使用了$分隔符(例如，mypojo$mytuple$f0)。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (181, 'function', '内置函数', '分组函数', 'GROUP_ID()', '返回唯一标识分组键组合的整数', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (182, 'function', '内置函数', '分组函数', 'GROUPING(expression1 [, expression2]* )\r\nGROUPING_ID(expression1 [, expression2]* )', '返回给定分组表达式的位向量。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (183, 'function', '内置函数', 'hash函数', 'MD5(string)', '以32位十六进制数字的字符串形式返回string的MD5哈希值;如果字符串为NULL，则返回NULL。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (184, 'function', '内置函数', 'hash函数', 'SHA1(string)', '返回字符串的SHA-1散列，作为一个由40个十六进制数字组成的字符串;如果字符串为NULL，则返回NULL', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (185, 'function', '内置函数', 'hash函数', 'SHA224(string)', '以56位十六进制数字的字符串形式返回字符串的SHA-224散列;如果字符串为NULL，则返回NULL。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (186, 'function', '内置函数', 'hash函数', 'SHA256(string)', '以64位十六进制数字的字符串形式返回字符串的SHA-256散列;如果字符串为NULL，则返回NULL。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (187, 'function', '内置函数', 'hash函数', 'SHA384(string)', '以96个十六进制数字的字符串形式返回string的SHA-384散列;如果字符串为NULL，则返回NULL。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (188, 'function', '内置函数', 'hash函数', 'SHA512(string)', '以128位十六进制数字的字符串形式返回字符串的SHA-512散列;如果字符串为NULL，则返回NULL。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (189, 'function', '内置函数', 'hash函数', 'SHA2(string, hashLength)', '使用SHA-2哈希函数族(SHA-224、SHA-256、SHA-384或SHA-512)返回哈希值。第一个参数string是要散列的字符串，第二个参数hashLength是结果的位长度(224、256、384或512)。如果string或hashLength为NULL，则返回NULL。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (190, 'function', '内置函数', '聚合函数', 'COUNT([ ALL ] expression | DISTINCT expression1 [, expression2]*)', '默认情况下或使用ALL时，返回表达式不为空的输入行数。对每个值的唯一实例使用DISTINCT。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (191, 'function', '内置函数', '聚合函数', 'COUNT(*)\r\nCOUNT(1)', '返回输入行数。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (192, 'function', '内置函数', '聚合函数', 'AVG([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的平均值(算术平均值)。对每个值的唯一实例使用DISTINCT。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (193, 'function', '内置函数', '聚合函数', 'SUM([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，返回所有输入行表达式的和。对每个值的唯一实例使用DISTINCT。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (194, 'function', '内置函数', '聚合函数', 'MAX([ ALL | DISTINCT ] expression)', '默认情况下或使用关键字ALL，返回表达式在所有输入行中的最大值。对每个值的唯一实例使用DISTINCT。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (195, 'function', '内置函数', '聚合函数', 'MIN([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的最小值。对每个值的唯一实例使用DISTINCT。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (196, 'function', '内置函数', '聚合函数', 'STDDEV_POP([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的总体标准差。对每个值的唯一实例使用DISTINCT。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (197, 'function', '内置函数', '聚合函数', 'STDDEV_SAMP([ ALL | DISTINCT ] expression)', '默认情况下或使用关键字ALL时，返回表达式在所有输入行中的样本标准差。对每个值的唯一实例使用DISTINCT。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (198, 'function', '内置函数', '聚合函数', 'VAR_POP([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的总体方差(总体标准差的平方)。对每个值的唯一实例使用DISTINCT。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (199, 'function', '内置函数', '聚合函数', 'VAR_SAMP([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，返回表达式在所有输入行中的样本方差(样本标准差的平方)。对每个值的唯一实例使用DISTINCT。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (200, 'function', '内置函数', '聚合函数', 'COLLECT([ ALL | DISTINCT ] expression)', '默认情况下，或使用关键字ALL，跨所有输入行返回表达式的多集。空值将被忽略。对每个值的唯一实例使用DISTINCT。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (201, 'function', '内置函数', '聚合函数', 'VARIANCE([ ALL | DISTINCT ] expression)', 'VAR_SAMP的同义词。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (202, 'function', '内置函数', '聚合函数', 'RANK()', '返回值在一组值中的秩。结果是1加上分区顺序中位于当前行之前或等于当前行的行数。这些值将在序列中产生空白。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (203, 'function', '内置函数', '聚合函数', 'DENSE_RANK()', '返回值在一组值中的秩。结果是1加上前面分配的秩值。与函数rank不同，dense_rank不会在排序序列中产生空隙。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (204, 'function', '内置函数', '聚合函数', 'ROW_NUMBER()', '根据窗口分区中的行顺序，为每一行分配一个惟一的连续数字，从1开始。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (205, 'function', '内置函数', '聚合函数', 'LEAD(expression [, offset] [, default] )', '返回表达式在窗口中当前行之前的偏移行上的值。offset的默认值是1,default的默认值是NULL。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (206, 'function', '内置函数', '聚合函数', 'LAG(expression [, offset] [, default])', '返回表达式的值，该值位于窗口中当前行之后的偏移行。offset的默认值是1,default的默认值是NULL。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (207, 'function', '内置函数', '聚合函数', 'FIRST_VALUE(expression)', '返回一组有序值中的第一个值。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (208, 'function', '内置函数', '聚合函数', 'LAST_VALUE(expression)', '返回一组有序值中的最后一个值。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (209, 'function', '内置函数', '聚合函数', 'LISTAGG(expression [, separator])', '连接字符串表达式的值，并在它们之间放置分隔符值。分隔符没有添加在字符串的末尾。分隔符的默认值是\'，\'。仅在blink planner中支持。', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (210, 'function', '内置函数', '列函数', 'withColumns(…)', '选择的列', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (211, 'function', '内置函数', '列函数', 'withoutColumns(…)', '不选择的列', '1.12', 0, 1, '2021-02-22 15:46:48', '2021-02-22 15:47:21');
INSERT INTO `dlink_flink_document` VALUES (262, 'function', 'UDF', '表值聚合函数', 'TO_MAP(string1,object2[, string3])', '将非规则一维表转化为规则二维表，string1是key。string2是value。string3为非必填项，表示key的值域（维度），用英文逗号分割。', '1.12', 8, 1, '2021-05-20 19:59:22', '2021-05-20 20:00:54');

-- ----------------------------
-- Table structure for dlink_history
-- ----------------------------
DROP TABLE IF EXISTS `dlink_history`;
CREATE TABLE `dlink_history`  (
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
  INDEX `task_index`(`task_id`) USING BTREE,
  INDEX `cluster_index`(`cluster_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '执行历史' ROW_FORMAT = Dynamic;

ALTER TABLE `dlink`.`dlink_task`
ADD COLUMN `config` text NULL COMMENT '配置' AFTER `cluster_id`;

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
  `driver_class_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'Driver',
  `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'IP',
  `port` int(11) NULL DEFAULT NULL COMMENT '端口号',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'url',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注释',
  `db_version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本，如oracle的11g，hbase的2.2.3',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `db_index`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
